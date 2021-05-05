package com.beme.tutorial.kotlinspringboot.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

// 전체 Application Context를 Initialize할 수 있음
// 부분적으로 객체를 초기화시키는 등의 전략으로 Spring Boot Test를 해줘야 됨
// @SpringBootTest:Application Bean만 init
// @AutoConfigureMockMvc: MockMvc bean init
@SpringBootTest
@AutoConfigureMockMvc
internal class BankControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should return all banks`() {
        // when
        mockMvc.get("/api/banks")
            .andDo { print() }
            //then
            .andExpect { status { isOk() } }
    }
}