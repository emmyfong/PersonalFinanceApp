package com.example.personalfinanceapp.transaction

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.Date

//data model for transactions
data class Transaction (
    //DocumentId tells Firestore that this field holds unique documentid
    @DocumentId
    val id: String = "",
    //link the transaction to auth user's id
    val userId: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val type: String = "expense",
    val date: Timestamp = Timestamp.now(),
    val isGoal: Boolean = false
)