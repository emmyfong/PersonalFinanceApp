package com.example.personalfinanceapp.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinanceapp.auth.AuthViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//manage state and logic for the transaction screen
//controls the trigger functions
class TransactionViewModel(
    private val repo: TransactionRepository = TransactionRepository(),
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _categoryCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val categoryCounts: StateFlow<Map<String, Int>> = _categoryCounts

    private val _categoryFilter = MutableStateFlow<String?>("All")
    val categoryFilter: StateFlow<String?> = _categoryFilter


    init {
        fetchCategories()
        fetchCategoryCounts()

        viewModelScope.launch {
            // Combine the current user status with the filter selection
            combine(
                authViewModel.user,
                _categoryFilter
            ) { user, categoryFilter ->
                Pair(user, categoryFilter)
            }
                .flatMapLatest { (user, categoryFilter) ->
                if (user != null) {
                    // If the user is logged in, return the repository's Flow
                    repo.getTransactions(categoryFilter)
                } else {
                    // If user is null, return an empty flow
                    kotlinx.coroutines.flow.flowOf(emptyList())
                }
            }
                // Collect the results from the flatMapLatest flow
                .collect { transactions ->
                    Log.i("FilterDebug", "Data Final Update - Count: ${transactions.size}")
                    _transactions.value = transactions

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
            withContext(NonCancellable) {
                for (name in defaults) {
                    repo.addCategory(name)
                }
            }
            //refresh state after creation
            fetchCategories()
        }
    }

    //set the filter -> trigger refresh
    fun setCategoryFilter(category: String?) {
        Log.d("FilterDebug", "UI called setCategoryFilter: $category")
        _categoryFilter.value = category
    }
}