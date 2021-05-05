package com.beme.tutorial.kotlinspringboot.service

import com.beme.tutorial.kotlinspringboot.datasource.BankDataSource
import com.beme.tutorial.kotlinspringboot.model.Bank
import org.springframework.stereotype.Service

@Service
class BankService(private val bankDataSource: BankDataSource) {
    fun getBanks() = bankDataSource.retrieveBanks()
    fun getBank(accountNumber: String) = bankDataSource.retrieveBank(accountNumber)
    fun addBank(bank: Bank) = bankDataSource.createBank(bank)
    fun updateBank(bank: Bank) = bankDataSource.updateBank(bank)
    fun deleteBank(accountNumber: String) = bankDataSource.deleteBank(accountNumber)
//        bankDataSource.retrieveBanks()
//        .first { it.accountNumber == accountNumber }
    // bankDataSource.retrieveBanks()
    //        .find { it.accountNumber == accountNumber } ?: throw IllegalArgumentException("에바 참치")
}