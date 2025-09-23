package com.example.personalfinanceapp.transaction

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Has functions to add/fetch transactions for current user
class TransactionRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    //ref to transactions collection
    private val transactionCollection = firestore.collection("transactions")
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
    fun getTransactions(): Flow<List<Transaction>> {
        val userId = currentUserId ?: return kotlinx.coroutines.flow.emptyFlow()

        return transactionCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Transaction::class.java)
            }
            .catch { e ->
                //log errors
                println("Error fetching transactions: $e")
                emit(emptyList())
            }
    }
}