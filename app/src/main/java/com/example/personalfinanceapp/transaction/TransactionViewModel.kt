package com.example.personalfinanceapp.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//manage state and logic for the transaction screen
class TransactionViewModel(
    private val repo: TransactionRepository = TransactionRepository()
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchTransactions()
    }

    // Fetches all transactions from the repository
    fun fetchTransactions() {
        viewModelScope.launch {
            repo.getTransactions()
                .collect {
                    _transactions.value = it
                }
        }
    }

    // Adds a new transaction
    fun addTransaction(amount: Double, category: String, type: String) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    amount = amount,
                    category = category,
                    type = type
                )
                repo.addTransaction(transaction)
                // Optionally refetch or update the local list after adding
                fetchTransactions()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    //fetch all categories
    fun fetchCategories() {
        viewModelScope.launch {
            repo.getCategories()
                .collect {
                    _categories.value = it.map { category -> category.name }
                }
        }
    }

    // add new category
    fun addCategory(name: String) {
        viewModelScope.launch {
            try {
                repo.addCategory(name)
                fetchCategories()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}