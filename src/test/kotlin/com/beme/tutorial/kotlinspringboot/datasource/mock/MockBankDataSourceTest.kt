package com.beme.tutorial.kotlinspringboot.datasource.mock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MockBankDataSourceTest {
    private val mockBankDataSource = MockBankDataSource()

    @Test
    fun `should provides a collection of banks`() {
        // when (act)
        val banks = mockBankDataSource.retrieveBanks()

        // then (assert)
        assertThat(banks.size).isGreaterThanOrEqualTo(3)
    }

    @Test
    fun `should provide mock data`() {
        // when
        val banks = mockBankDataSource.retrieveBanks()

        // then
        assertThat(banks).allMatch { it.accountNumber.isNotBlank() }
        assertThat(banks).anyMatch { it.trust != 0.0 }
        assertThat(banks).anyMatch { it.transactionFee != 0 }
    }
}