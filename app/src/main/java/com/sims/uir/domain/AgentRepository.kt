package com.sims.uir.domain

import kotlinx.coroutines.flow.Flow

interface AgentRepository {
    suspend fun ensureActiveIdentity(): AgentIdentity
    fun observeActiveIdentity(): Flow<AgentIdentity?>
    fun observeMemories(): Flow<List<AgentMemory>>
    fun observeTasks(): Flow<List<AgentTask>>
    fun observeSnapshots(): Flow<List<AgentSnapshot>>
    suspend fun addMemory(title: String, content: String)
    suspend fun addTask(title: String, details: String)
    suspend fun completeTask(taskId: Long)
    suspend fun appendEvent(eventType: String, message: String, payload: String = "")
    suspend fun createSnapshot(label: String): AgentSnapshot
    suspend fun restoreSnapshot(snapshotId: Long): Boolean
    suspend fun restoreLatestSnapshot(): Boolean
    suspend fun listSnapshots(): List<AgentSnapshot>
}
