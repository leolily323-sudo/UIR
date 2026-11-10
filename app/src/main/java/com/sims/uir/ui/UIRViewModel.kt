package com.sims.uir.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sims.uir.diagnostics.DiagnosticsManager
import com.sims.uir.domain.AgentSnapshot
import com.sims.uir.domain.AgentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class UIRUiState(
    val identityName: String = "Loading...",
    val memoryCount: Int = 0,
    val taskCount: Int = 0,
    val diagnosticStatus: String = "Not run",
    val latestSnapshot: AgentSnapshot? = null,
)

class UIRViewModel(
    private val repository: AgentRepository,
    private val diagnosticsManager: DiagnosticsManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UIRUiState())
    val uiState: StateFlow<UIRUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.ensureActiveIdentity()
                combine(
                    repository.observeActiveIdentity(),
                    repository.observeMemories(),
                    repository.observeTasks(),
                    repository.observeSnapshots(),
                ) { identity, memories, tasks, snapshots ->
                    UIRUiState(
                        identityName = identity?.name ?: "No active identity",
                        memoryCount = memories.size,
                        taskCount = tasks.size,
                        diagnosticStatus = _uiState.value.diagnosticStatus,
                        latestSnapshot = snapshots.firstOrNull(),
                    )
                }.collect { databaseState ->
                    _uiState.value = databaseState.copy(
                        diagnosticStatus = _uiState.value.diagnosticStatus,
                    )
                }
            } catch (exception: Exception) {
                updateStatus("Storage error: ${exception.message ?: exception::class.simpleName}")
            }
        }
    }

    fun addMemory(title: String, content: String) {
        performAction("Memory saved") {
            repository.addMemory(title.trim(), content.trim())
        }
    }

    fun createSnapshot() {
        performAction("Snapshot created") {
            val label = "Snapshot ${System.currentTimeMillis()}"
            repository.createSnapshot(label)
        }
    }

    fun runDiagnostics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(diagnosticStatus = "Running diagnostics...")
            try {
                val result = diagnosticsManager.runDiagnostics()
                val counts = result.recordCounts
                val details = counts?.let {
                    " (identities ${it.identities}, memories ${it.memories}, tasks ${it.tasks}, " +
                        "events ${it.events}, snapshots ${it.snapshots})"
                }.orEmpty()
                updateStatus("${if (result.passed) "Passed" else "Failed"}: ${result.message}$details")
            } catch (exception: Exception) {
                updateStatus("Diagnostics error: ${exception.message ?: exception::class.simpleName}")
            }
        }
    }

    private fun performAction(successMessage: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
                updateStatus(successMessage)
            } catch (exception: Exception) {
                updateStatus("Error: ${exception.message ?: exception::class.simpleName}")
            }
        }
    }

    private fun updateStatus(status: String) {
        _uiState.value = _uiState.value.copy(diagnosticStatus = status)
    }
}
