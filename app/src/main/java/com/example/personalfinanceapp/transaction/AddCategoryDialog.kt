package com.example.personalfinanceapp.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddCategoryDialog(
    transactionViewModel: TransactionViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Category") },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        transactionViewModel.addCategory(categoryName)
                        onDismiss()
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

//dialog for editing/deleting
@Composable
fun EditCategoryDialog(
    categoryToEdit: String,
    onDismiss: () -> Unit,
    onConfirmEdit: (String, String) -> Unit
) {
    var newCategoryName by remember {mutableStateOf(categoryToEdit)}

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Category") },
        text = {
            OutlinedTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                label = { Text("New Category Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newCategoryName.isNotBlank() && newCategoryName != categoryToEdit) {
                        onConfirmEdit(categoryToEdit, newCategoryName)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteCategoryDialog(
    categoryToDelete: String,
    onDismiss: () -> Unit,
    onConfirmDelete: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Category") },
        text = { Text("This will update all transactions in '$categoryToDelete' to 'Uncategorized'. Are you sure you want to delete this category?")},
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDelete(categoryToDelete)
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}