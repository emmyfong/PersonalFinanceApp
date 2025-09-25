package com.example.personalfinanceapp.transaction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinanceapp.R
import com.example.personalfinanceapp.ui.theme.Black
import kotlinx.coroutines.selects.select

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(
    transactionViewModel: TransactionViewModel = viewModel(),
    onNavigateBack: () -> Unit,
) {
    val categoryNames by transactionViewModel.categories.collectAsState()
    val categoryCounts by transactionViewModel.categoryCounts.collectAsState()

    //states to manage dialog visibility
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Pair<String, Int>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_back_ios_new_24),
                            contentDescription = "Back",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Category")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Categories Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (categoryCounts.isEmpty()) {
                Text("No categories found. Click '+' to add one!")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoryNames) { categoryName ->
                        //lookup transaction count for curret category -> default 0
                        val count = categoryCounts[categoryName] ?: 0
                        CategoryItem(
                            categoryName = categoryName,
                            transactionCount = count,
                            onEdit = {
                                selectedCategory = Pair(categoryName, count)
                                showEditDialog = true
                            },
                            onDelete = {
                                selectedCategory = Pair(categoryName, count)
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
    // dialogs for editing/deleting
    if (showEditDialog && selectedCategory != null) {
        EditCategoryDialog(
            categoryToEdit = selectedCategory!!.first,
            onDismiss = {
                showEditDialog = false
                selectedCategory = null
            },
            onConfirmEdit = { oldName, newName ->
                transactionViewModel.editCategory(oldName, newName)
                showEditDialog = false
                selectedCategory = null
            }
        )
    }
    if (showDeleteDialog && selectedCategory != null) {
        DeleteCategoryDialog(
            categoryToDelete = selectedCategory!!.first,
            onDismiss = {
                showDeleteDialog = false
                selectedCategory = null
            },
            onConfirmDelete = { name ->
                transactionViewModel.deleteCategory(name)
                showDeleteDialog = false
                selectedCategory = null
            }
        )
    }
    if (showAddDialog) {
        AddCategoryDialog(
            transactionViewModel = transactionViewModel,
            onDismiss = { showAddDialog = false }
        )
    }
}


@Composable
fun CategoryItem(
    categoryName: String,
    transactionCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
    ) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Text(
                    text = "$transactionCount items",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Black
                )
            }
            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Category")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Category")
                }
            }
        }
    }
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