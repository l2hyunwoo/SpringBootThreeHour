package com.beme.tutorial.kotlinspringboot.datasource.mock

import com.beme.tutorial.kotlinspringboot.datasource.BankDataSource
import com.beme.tutorial.kotlinspringboot.model.Bank
import org.springframework.stereotype.Repository

@Repository
class MockBankDataSource : BankDataSource {
    private val banks = mutableListOf(
        Bank("1101110", 1.2, 3),
        Bank("1101111", 1.2, 3),
        Bank("1101112", 1.2, 0),
    )

    override fun retrieveBanks() = banks

    override fun retrieveBank(accountNumber: String) = banks.firstOrNull { it.accountNumber == accountNumber }
        ?: throw NoSuchElementException("Could not find any account with that accountNumber")

    override fun createBank(bank: Bank): Bank {
        if (banks.any { it.accountNumber == bank.accountNumber }) {
            throw IllegalArgumentException("Bank with account number ${bank.accountNumber} already exists")
        }
        banks.add(bank)
        return bank
    }

    override fun updateBank(bank: Bank): Bank {
        val currentBank = banks.firstOrNull { it.accountNumber == bank.accountNumber }
            ?: throw NoSuchElementException("Could not find any account with that accountNumber")

        banks.remove(currentBank)
        banks.add(bank)

        return bank
    }
}