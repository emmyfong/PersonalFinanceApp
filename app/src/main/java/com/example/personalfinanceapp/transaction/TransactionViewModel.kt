package com.example.personalfinanceapp.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinanceapp.auth.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//manage state and logic for the transaction screen
//controls the trigger functions
class TransactionViewModel(
    private val repo: TransactionRepository = TransactionRepository(),
    private val authViewModel: AuthViewModel = AuthViewModel()
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _categoryCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val categoryCounts: StateFlow<Map<String, Int>> = _categoryCounts

    init {
        fetchCategories()
        fetchCategoryCounts()

        viewModelScope.launch {
            authViewModel.user.collect { user ->
                if (user != null) {
                    if (_transactions.value.isEmpty()) {
                        fetchTransactions()
                        fetchCategories()
                        fetchCategoryCounts()
                    }
                }
            }
        }
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

    //edit category
    fun editCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            try {
                repo.editCategory(oldName, newName)
                fetchCategories()
                fetchCategoryCounts()
                fetchTransactions() //to refresh all the data
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    //delete category
    fun deleteCategory(name: String) {
        viewModelScope.launch {
            try {
                repo.deleteCategory(name)
                fetchCategories()
                fetchCategoryCounts()
                fetchTransactions() //to refresh all the data
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun fetchCategoryCounts() {
        viewModelScope.launch {
            repo.getTransactions()
            .collect { transactions ->
                val counts = transactions.groupingBy { it.category }.eachCount()
                _categoryCounts.value = counts
        }
        }
    }

    //add defult categories after user sign up
    fun addDefaultCategoriesOnSignUp() {
        val defaults = listOf("Groceries", "Rent", "Salary", "Utilities", "Other")
        viewModelScope.launch {
            defaults.forEach { name ->
                repo.addCategory(name)
            }
            //refresh state after creation
            fetchCategories()
        }
    }
}