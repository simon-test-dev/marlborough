package com.marlborough

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.marlborough.domain.member.dto.UserForm
import com.marlborough.domain.member.dto.UserResult
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.Cookie


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@Slf4j
//@Import(value = [SecurityTestConfig::class])
class MarlboroughApplicationTests @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @Test
    fun `Hello Page & 회원 가입 & 회원 로그인`() {
        val objectMapper: ObjectMapper = ObjectMapper()
        objectMapper.registerModule(KotlinModule())

        mockMvc.perform(get("/hello"))
            .andExpect(status().isOk)
            .andExpect(content().string("hello"))
            .andDo(print())

        mockMvc.perform(
            post("/user/join")
                .content(objectMapper.writeValueAsBytes(UserForm("admin", "admin")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("1"))
            .andDo(print())

        val userInfo = mockMvc.perform(
            post("/user/login")
                .content(objectMapper.writeValueAsBytes(UserForm("admin", "admin")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(print())
            .andReturn()
        val userResult = objectMapper.readValue(userInfo.response.contentAsString, UserResult::class.java)

        assert(userResult.userId == "admin")
    }
}
