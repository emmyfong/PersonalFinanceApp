package com.example.personalfinanceapp.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinanceapp.auth.AuthViewModel
import com.example.personalfinanceapp.transaction.TransactionViewModel
import com.example.personalfinanceapp.transaction.AddCategoryDialog
import com.example.personalfinanceapp.transaction.DeleteCategoryDialog
import com.example.personalfinanceapp.transaction.EditCategoryDialog
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel,
    dashboardViewModel: DashboardViewModel,
) {
    val userName = authViewModel.user.collectAsState().value?.displayName ?: "User"
    val summary by dashboardViewModel.summary.collectAsState()
    val categoryNames by transactionViewModel.categories.collectAsState()
    val categoryCounts by transactionViewModel.categoryCounts.collectAsState()
    val allTimeCategoryNet by dashboardViewModel.allTimeCategoryNet.collectAsState()


    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Pair<String, Int>?>(null) }


    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //Summary/Header
            item {
                Column(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                ) {
                    //Hello User
                    Text(
                        text = "Hello, $userName!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Main App Title/Header Text
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Welcome/Status Text
                    Text(
                        text = "Welcome! You're logged in 🎉",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    //Networth
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total Net Worth", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
                            Text(
                                text = "$%.2f".format(summary.netWorth),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    //monthly summary
                    Text("Monthly Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Income Card
                        SummaryValueCard(
                            label = "Income",
                            amount = summary.monthlyIncome,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        // Expense Card
                        SummaryValueCard(
                            label = "Expense",
                            amount = summary.monthlyExpense,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            //categories section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    //button to go to full manager for edit/delete
                    IconButton(onClick = {showAddDialog = true}) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Category")
                    }
                }
                if (categoryNames.isEmpty()) {
                    Text("No categories found. Click '+' to add one!")
                }
            }

            //categories list
            items(categoryNames) { categoryName ->
                val itemCount = categoryCounts[categoryName] ?: 0
                val monthlyExpenseAmount = summary.categoryBreakdown[categoryName] ?: 0.0
                val allTimeNetAmount = allTimeCategoryNet[categoryName] ?: 0.0

                CategoryItem(
                    categoryName = categoryName,
                    transactionCount = itemCount,
                    monthlyExpense = monthlyExpenseAmount,
                    allTimeNet = allTimeNetAmount,
                    onEdit = {
                        selectedCategory = Pair(categoryName, itemCount)
                        showEditDialog = true
                    },
                    onDelete = {
                        selectedCategory = Pair(categoryName, itemCount)
                        showDeleteDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            transactionViewModel = transactionViewModel,
            onDismiss = { showAddDialog = false }
        )
    }
    if (showEditDialog && selectedCategory != null) {
        EditCategoryDialog(
            categoryToEdit = selectedCategory!!.first,
            onDismiss = { showEditDialog = false; selectedCategory = null },
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
            onDismiss = { showDeleteDialog = false; selectedCategory = null },
            onConfirmDelete = { name ->
                transactionViewModel.deleteCategory(name)
                showDeleteDialog = false
                selectedCategory = null
            }
        )
    }

}

@Composable
fun CategoryItem(
    categoryName: String,
    transactionCount: Int,
    monthlyExpense: Double,
    allTimeNet: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //left column -> category name and item count
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$transactionCount items",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            //right column -> show net amount
            val netPrefix = if (allTimeNet >= 0) "+" else "-"
            val netColor = if (allTimeNet >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

            Text(
                text = "$netPrefix$%.2f".format(abs(allTimeNet)), // Use absolute value for formatting
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = netColor
            )

            Spacer(modifier = Modifier.width(72.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
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

@Composable
fun SummaryValueCard(label: String, amount: Double, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.titleSmall, color = color)
            Text("$%.2f".format(amount), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}