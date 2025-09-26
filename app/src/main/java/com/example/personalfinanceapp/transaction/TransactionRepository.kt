package com.example.personalfinanceapp.transaction

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

//data class for categories
data class Category(
    val name: String = "",
    val userId: String = ""
)

// Has functions to add/fetch transactions for current user
class TransactionRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    //ref to transactions collection
    private val transactionCollection = firestore.collection("transactions")
    private val categoryCollection = firestore.collection("categories")
    private val currentUserId: String?
        get() = auth.currentUser?.uid

    //CREATE NEW TRANSACTION
    suspend fun addTransaction(transaction: Transaction) {
        if (currentUserId == null) throw IllegalStateException("User must be logged in to add a transaction")

        //make sure that the transaction is tagged with user id
        val transactionWithUser = transaction.copy(userId = currentUserId!!)

        transactionCollection
            .add(transactionWithUser)
            .await()
    }

    //FETCH ALL TRANSACTIONS (READ)
// In TransactionRepository.kt

    fun getTransactions(categoryFilter: String? = null): Flow<List<Transaction>> {
        //Get User ID or return empty flow
        val userId = currentUserId ?: run {
            Log.e("FilterDebug", "UserID is null, cannot fetch.")
            return kotlinx.coroutines.flow.emptyFlow()
        }

        //base query: filter by userId
        var query: Query = transactionCollection.whereEqualTo("userId", userId)

        if (categoryFilter != null && categoryFilter != "All") {
            query = query.whereEqualTo("category", categoryFilter)
            Log.d("FilterDebug", "Query Built: Filtering by Category '$categoryFilter'")
        } else {
            Log.d("FilterDebug", "Query Built: Filtering for ALL transactions")
        }

        query = query.orderBy("date", Query.Direction.DESCENDING)

        return query
            .snapshots()
            .map { snapshot ->
                Log.v("FilterDebug", "Firestore Snapshot Size: ${snapshot.documents.size}")
                snapshot.toObjects(Transaction::class.java)
            }
            .catch { e ->
                Log.e("FilterDebug", "Firestore Query Failed: ${e.message}")
                println("Error fetching transactions: $e")
                emit(emptyList())
            }
    }

    //FETCH CATEGORIES
    fun getCategories(): Flow<List<Category>> {
        val userId = currentUserId ?: return kotlinx.coroutines.flow.emptyFlow()

        return categoryCollection
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Category::class.java)
            }
            .catch { e ->
                println("Error fetching categories: $e")
                emit(emptyList())
            }
    }

    //CREATE NEW CATEGORY
    suspend fun addCategory(name: String) {
        if (currentUserId == null) throw IllegalStateException("User must be logged in to add a category")

        //check if this category already exists for this user
        val existingCategory = categoryCollection
            .whereEqualTo("userId", currentUserId!!)
            .whereEqualTo("name", name)
            .get()
            .await()
            .documents
            .firstOrNull()

        //only add if the category doesnt exist
        if (existingCategory == null) {
            val newCategory = Category(name = name, userId = currentUserId!!)
            categoryCollection
                .add(newCategory)
                .await()
        }
    }

    //UPDATE CATEGORY
    suspend fun editCategory(oldName: String, newName: String) {
        if (currentUserId == null) throw IllegalStateException("User must be logged in to edit a category")

        val batch = firestore.batch()

        //update all transactions with the old category name -> new category name
        transactionCollection
            .whereEqualTo("userId", currentUserId)
            .whereEqualTo("category", oldName)
            .get()
            .await()
            .documents
            .forEach { document ->
                batch.update(document.reference, "category", newName)
            }

        //update category document
        categoryCollection
            .whereEqualTo("userId", currentUserId)
            .whereEqualTo("name", oldName)
            .get()
            .await()
            .documents
            .firstOrNull()?.reference
            ?.let { categoryRef ->
                batch.update(categoryRef, "name", newName)
            }

        batch.commit().await()
    }

    //DELETE CATEGORY
    suspend fun deleteCategory(name: String) {
        if (currentUserId == null) throw IllegalStateException("User must be logged in to delete a category")

        val batch = firestore.batch()

        //update all transactions with deleted category to "uncategorized"
        transactionCollection
            .whereEqualTo("userId", currentUserId!!)
            .whereEqualTo("category", name)
            .get()
            .await()
            .documents
            .forEach { document ->
                batch.update(document.reference, "category", "Uncategorized")
            }

        //delete the category document
        categoryCollection
            .whereEqualTo("userId", currentUserId!!)
            .whereEqualTo("name", name)
            .get()
            .await()
            .documents
            .firstOrNull()?.reference
            ?.let { categoryRef ->
                batch.delete(categoryRef)
            }

        batch.commit().await()
    }
}