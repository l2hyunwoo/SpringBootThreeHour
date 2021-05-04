package service

import com.beme.tutorial.kotlinspringboot.datasource.BankDataSource
import org.springframework.stereotype.Service

@Service
class BankService(private val bankDataSource: BankDataSource) {
    fun getBanks() = bankDataSource.retrieveBanks()
}