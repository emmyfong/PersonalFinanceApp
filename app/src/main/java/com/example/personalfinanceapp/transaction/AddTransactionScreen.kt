package com.example.personalfinanceapp.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinanceapp.ui.theme.Black
import com.example.personalfinanceapp.ui.theme.SubText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionViewModel: TransactionViewModel = viewModel(),
    onTransactionAdded: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Expense") }

    val defaultCategories = listOf("Groceries", "Rent", "Salary", "Utilities", "Other")
    val transactionTypes = listOf("Expense", "Income")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            "New Transaction",
            style = MaterialTheme.typography.headlineLarge,
            color = Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Amount Input
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount", color = SubText) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Transaction Type Dropdown
        DropdownMenuField(
            label = "Type",
            options = transactionTypes,
            selectedOption = selectedType,
            onOptionSelected = { selectedType = it }
        )

        // Category Dropdown
        DropdownMenuField(
            label = "Category",
            options = defaultCategories,
            selectedOption = selectedCategory,
            onOptionSelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Transaction Button
        Button(
            onClick = {
                //check if amount string contains decimal point else add it
                val formattedAmountString = if (!amount.contains(".")){
                    "$amount.00"
                } else {
                    amount
                }

                val transactionAmount = formattedAmountString.toDoubleOrNull()
                if (transactionAmount != null && selectedCategory.isNotEmpty()) {
                    transactionViewModel.addTransaction(
                        amount = transactionAmount,
                        category = selectedCategory,
                        type = selectedType.lowercase()
                    )
                    onTransactionAdded()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Add Transaction", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (selectedOption.isNotEmpty()) selectedOption else "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = SubText) },
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