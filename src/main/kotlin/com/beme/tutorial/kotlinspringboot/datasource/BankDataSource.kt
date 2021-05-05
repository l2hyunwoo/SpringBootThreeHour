package com.beme.tutorial.kotlinspringboot.datasource

import com.beme.tutorial.kotlinspringboot.model.Bank

interface BankDataSource {
    fun retrieveBanks(): Collection<Bank>
    fun retrieveBank(accountNumber: String): Bank
}