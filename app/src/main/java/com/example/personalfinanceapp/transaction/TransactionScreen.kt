package com.example.personalfinanceapp.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
    transactionViewModel: TransactionViewModel,
    onNavigateToAddTransaction: () -> Unit,
) {
    val transactions by transactionViewModel.transactions.collectAsState()

    val categories by transactionViewModel.categories.collectAsState()
    val selectedFilter by transactionViewModel.categoryFilter.collectAsState()

    val filterOptions = remember(categories) {
        listOf("All") + categories
    }

    Scaffold(
        topBar = {
          TopAppBar(
              title = { Text("Transaction History") },
          )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTransaction) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Transaction")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            FilterDropdownMenu(
                options = filterOptions,
                selectedOption = selectedFilter ?: "All",
                onOptionSelected = { newFilter ->
                    transactionViewModel.setCategoryFilter(newFilter)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (transactions.isEmpty()) {
                Text(
                    "No transactions found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SubText
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
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

            //check to see if + or - and change color based on that
            val prefix = if (transaction.type == "income") "+" else "-"
            val amountColor = if (transaction.type == "income") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

            //format amount to have 2 decimal places
            val formattedAmount = "%.2f".format(transaction.amount)

            Text(
                text = "$prefix$$formattedAmount",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(0.5f)
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Filter by Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}