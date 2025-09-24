package com.example.personalfinanceapp.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinanceapp.ui.theme.Black
import com.example.personalfinanceapp.ui.theme.SubText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactionViewModel: TransactionViewModel = viewModel(),
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToManageCategories: () -> Unit
) {
    val transactions by transactionViewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
          TopAppBar(
              title = { Text("Categories") },
              actions = {
                  IconButton(onClick = onNavigateToManageCategories) {
                      Icon(Icons.Filled.Add, contentDescription = "Manage Categories")
                  }
              }
          )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTransaction) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Transaction History",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (transactions.isEmpty()) {
                Text(
                    "No transactions found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SubText
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(transaction.date.seconds * 1000))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = SubText
                )
            }
            Text(
                text = "$${transaction.amount}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == "income") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
            )
        }
    }
}