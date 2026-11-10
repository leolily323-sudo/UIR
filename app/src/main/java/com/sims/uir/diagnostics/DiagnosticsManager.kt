package com.sims.uir.diagnostics

import com.sims.uir.data.local.EventEntity
import com.sims.uir.data.local.UirDatabase
import com.sims.uir.domain.DiagnosticRecordCounts
import com.sims.uir.domain.DiagnosticResult

class DiagnosticsManager(
    private val database: UirDatabase,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend fun runDiagnostics(): DiagnosticResult =
        try {
            val sqliteDatabase = database.openHelper.writableDatabase
            check(sqliteDatabase.isOpen) { "Database did not open" }

            database.eventDao().insert(
                EventEntity(
                    eventType = "diagnostics",
                    message = "Database diagnostics completed",
                    payload = "",
                    createdAt = clock(),
                ),
            )

            val counts = DiagnosticRecordCounts(
                identities = database.identityDao().count(),
                memories = database.memoryDao().count(),
                tasks = database.taskDao().count(),
                events = database.eventDao().count(),
                snapshots = database.snapshotDao().count(),
            )
            DiagnosticResult(
                passed = true,
                message = "Database ready",
                recordCounts = counts,
            )
        } catch (exception: Exception) {
            DiagnosticResult(
                passed = false,
                message = "Database diagnostics failed: ${exception.message ?: exception::class.simpleName}",
            )
        }
}
