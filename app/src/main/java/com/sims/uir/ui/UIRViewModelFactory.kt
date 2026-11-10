package com.sims.uir.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sims.uir.diagnostics.DiagnosticsManager
import com.sims.uir.domain.AgentRepository

class UIRViewModelFactory(
    private val repository: AgentRepository,
    private val diagnosticsManager: DiagnosticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(UIRViewModel::class.java)) {
            "Unsupported ViewModel class: ${modelClass.name}"
        }
        return modelClass.cast(UIRViewModel(repository, diagnosticsManager))
    }
}
