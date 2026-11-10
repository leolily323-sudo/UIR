package com.sims.uir.domain

data class DiagnosticResult(
    val passed: Boolean,
    val message: String,
    val recordCounts: DiagnosticRecordCounts? = null,
)

data class DiagnosticRecordCounts(
    val identities: Int,
    val memories: Int,
    val tasks: Int,
    val events: Int,
    val snapshots: Int,
)
