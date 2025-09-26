package com.example.personalfinanceapp.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinanceapp.transaction.Transaction
import com.example.personalfinanceapp.transaction.TransactionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Month
import java.time.ZonedDateTime

//fetches all transactions from TransactionRepository

data class MonthlySummary(
    val netWorth: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val categoryBreakdown: Map<String, Double> = emptyMap()
)

data class CategoryNetValue(
    val category: String,
    val netValue: Double
)

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel (
    private val transactionViewModel: TransactionViewModel
) : ViewModel() {
    private val _summary = MutableStateFlow(MonthlySummary())
    val summary: StateFlow<MonthlySummary> = _summary

    private val _allTimeCategoryNet = MutableStateFlow<Map<String, Double>>(emptyMap())
    val allTimeCategoryNet: StateFlow<Map<String, Double>> = _allTimeCategoryNet

    init {
        calculateSummary()
    }

    private fun calculateSummary() {
        viewModelScope.launch {
            transactionViewModel.transactions.collect { transactions ->
                val currentMonth = ZonedDateTime.now().month

                var totalIncome = 0.0
                var totalExpense = 0.0
                val expenseByCategory = mutableMapOf<String, Double>()

                var monthlyIncome = 0.0
                var monthlyExpense = 0.0

                val allTimeNet = transactions.groupBy { it.category }.mapValues { (_, categoryTransactions) ->
                    categoryTransactions.sumOf {
                        if (it.type == "income") it.amount else -it.amount
                    }
                }
                _allTimeCategoryNet.value = allTimeNet

                for (t in transactions) {
                    val transactionDateTime = ZonedDateTime.ofInstant(
                        t.date.toDate().toInstant(),
                        ZonedDateTime.now().zone
                    )

                    // Calculate Net Worth
                    if (t.type == "income") {
                        totalIncome += t.amount
                    } else {
                        totalExpense += t.amount
                    }

                    // Monthly Breakdown
                    if (transactionDateTime.month == currentMonth) {
                        if (t.type == "income") {
                            monthlyIncome += t.amount
                        } else {
                            monthlyExpense += t.amount

                            val currentCategoryExpense = expenseByCategory.getOrDefault(t.category, 0.0)
                            expenseByCategory[t.category] = currentCategoryExpense + t.amount
                        }
                    }
                }

                _summary.value = MonthlySummary(
                    netWorth = totalIncome - totalExpense,
                    monthlyIncome = monthlyIncome,
                    monthlyExpense = monthlyExpense,
                    categoryBreakdown = expenseByCategory
                )
            }
        }
    }

}