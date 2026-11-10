package com.sims.uir.di

import android.content.Context
import androidx.room.Room
import com.sims.uir.data.RoomAgentRepository
import com.sims.uir.data.local.UirDatabase
import com.sims.uir.diagnostics.DiagnosticsManager
import com.sims.uir.domain.AgentRepository

data class AppDependencies(
    val database: UirDatabase,
    val repository: AgentRepository,
    val diagnosticsManager: DiagnosticsManager,
)

object AppContainer {
    @Volatile
    private var dependencies: AppDependencies? = null

    fun get(context: Context): AppDependencies =
        dependencies ?: synchronized(this) {
            dependencies ?: run {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    UirDatabase::class.java,
                    "uir_database",
                ).build()
                AppDependencies(
                    database = database,
                    repository = RoomAgentRepository(database),
                    diagnosticsManager = DiagnosticsManager(database),
                ).also { dependencies = it }
            }
        }
}
