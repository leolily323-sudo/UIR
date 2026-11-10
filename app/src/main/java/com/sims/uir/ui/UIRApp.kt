package com.sims.uir.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sims.uir.domain.AgentRepository
import java.text.DateFormat
import java.util.Date
import com.sims.uir.diagnostics.DiagnosticsManager

@Composable
fun UIRApp(
    repository: AgentRepository,
    diagnosticsManager: DiagnosticsManager,
) {
    val viewModel: UIRViewModel = viewModel(
        factory = UIRViewModelFactory(repository, diagnosticsManager),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showMemoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("UIR", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Universal Identity Runtime",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        StatusCard("Identity Status", state.identityName)
        StatusCard("Memory Count", state.memoryCount.toString())
        StatusCard("Task Count", state.taskCount.toString())

        state.latestSnapshot?.let { snapshot ->
            StatusCard(
                title = "Latest Snapshot",
                value = "${snapshot.label}\n${DateFormat.getDateTimeInstance().format(Date(snapshot.createdAt))}",
            )
        }

        Text(
            text = "Diagnostics: ${state.diagnosticStatus}",
            style = MaterialTheme.typography.bodyMedium,
        )

        Button(
            onClick = { showMemoryDialog = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add Memory")
        }
        Button(
            onClick = viewModel::createSnapshot,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Create Snapshot")
        }
        Button(
            onClick = viewModel::runDiagnostics,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Run Diagnostics")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showMemoryDialog) {
        AddMemoryDialog(
            onDismiss = { showMemoryDialog = false },
            onSave = { title, content ->
                viewModel.addMemory(title, content)
                showMemoryDialog = false
            },
        )
    }
}

@Composable
private fun AddMemoryDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, content: String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Memory") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    minLines = 3,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, content) },
                enabled = title.isNotBlank() && content.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun StatusCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
