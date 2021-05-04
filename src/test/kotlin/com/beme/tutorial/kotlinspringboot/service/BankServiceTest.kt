package com.beme.tutorial.kotlinspringboot.service

import com.beme.tutorial.kotlinspringboot.datasource.BankDataSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BankServiceTest {
    private val dataSource: BankDataSource = mockk()
    private val bankService = BankService(dataSource)
    @Test
    fun `should call its DataSource to retrieve banks`() {
        // given
        every { dataSource.retrieveBanks() } returns emptyList()

        // when
        val banks = bankService.getBanks()

        // then
        // mockk의 기능
        // bankService.getBanks() 을 호출하면서 최소한 1번 이상의 dataSource.retrieveBanks()가 호출되었다.
        verify(exactly = 1) { dataSource.retrieveBanks() }
        assertThat(banks).isEmpty()
    }
}