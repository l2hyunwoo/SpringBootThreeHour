package com.beme.tutorial.kotlinspringboot.datasource.mock

import com.beme.tutorial.kotlinspringboot.datasource.BankDataSource
import com.beme.tutorial.kotlinspringboot.model.Bank
import org.springframework.stereotype.Repository

@Repository
class MockBankDataSource : BankDataSource {
    private val banks = listOf(
        Bank("1101110", 1.2, 3),
        Bank("1101110", 1.2, 3),
        Bank("1101110", 1.2, 0),
    )

    override fun retrieveBanks() = banks
}