package com.sims.uir.data

import androidx.room.withTransaction
import com.sims.uir.data.local.EventEntity
import com.sims.uir.data.local.IdentityEntity
import com.sims.uir.data.local.MemoryEntity
import com.sims.uir.data.local.SnapshotEntity
import com.sims.uir.data.local.TaskEntity
import com.sims.uir.data.local.UirDatabase
import com.sims.uir.domain.AgentIdentity
import com.sims.uir.domain.AgentMemory
import com.sims.uir.domain.AgentRepository
import com.sims.uir.domain.AgentSnapshot
import com.sims.uir.domain.AgentTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RoomAgentRepository(
    private val database: UirDatabase,
    private val clock: () -> Long = System::currentTimeMillis,
    private val json: Json = Json,
) : AgentRepository {
    override suspend fun ensureActiveIdentity(): AgentIdentity =
        database.withTransaction { getOrCreateActiveIdentity() }.toDomain()

    private suspend fun getOrCreateActiveIdentity(): IdentityEntity =
        database.identityDao().getActive() ?: run {
            val now = clock()
            val identity = IdentityEntity(
                name = "Local Identity",
                persona = "A private, local-first assistant.",
                values = "Privacy, reliability, user control",
                goals = "Support the user's local runtime",
                version = 1,
                isActive = true,
                createdAt = now,
                updatedAt = now,
            )
            val identityId = database.identityDao().insert(identity)
            identity.copy(id = identityId)
        }

    override fun observeActiveIdentity(): Flow<AgentIdentity?> =
        database.identityDao().observeActive().map { it?.toDomain() }

    override fun observeMemories(): Flow<List<AgentMemory>> =
        database.memoryDao().observeAll().map { memories -> memories.map { it.toDomain() } }

    override fun observeTasks(): Flow<List<AgentTask>> =
        database.taskDao().observeAll().map { tasks -> tasks.map { it.toDomain() } }

    override fun observeSnapshots(): Flow<List<AgentSnapshot>> =
        database.snapshotDao().observeAll().map { snapshots -> snapshots.map { it.toDomain() } }

    override suspend fun addMemory(title: String, content: String) {
        val now = clock()
        database.memoryDao().insert(
            MemoryEntity(
                title = title,
                content = content,
                kind = "note",
                importance = 1,
                source = "user",
                createdAt = now,
                updatedAt = now,
            ),
        )
        appendEvent("memory_added", "Memory added: $title")
    }

    override suspend fun addTask(title: String, details: String) {
        val now = clock()
        database.taskDao().insert(
            TaskEntity(
                title = title,
                details = details,
                isComplete = false,
                createdAt = now,
                completedAt = null,
            ),
        )
        appendEvent("task_added", "Task added: $title")
    }

    override suspend fun completeTask(taskId: Long) {
        database.taskDao().complete(taskId, clock())
        appendEvent("task_completed", "Task completed: $taskId")
    }

    override suspend fun appendEvent(eventType: String, message: String, payload: String) {
        database.eventDao().insert(
            EventEntity(
                eventType = eventType,
                message = message,
                payload = payload,
                createdAt = clock(),
            ),
        )
    }

    override suspend fun createSnapshot(label: String): AgentSnapshot =
        database.withTransaction {
            val identity = getOrCreateActiveIdentity()
            val memories = database.memoryDao().getAll()
            val tasks = database.taskDao().getAll()
            val snapshot = SnapshotEntity(
                label = label,
                identityJson = json.encodeToString(identity),
                memoriesJson = json.encodeToString(memories),
                tasksJson = json.encodeToString(tasks),
                createdAt = clock(),
            )
            val snapshotId = database.snapshotDao().insert(snapshot)
            snapshot.copy(id = snapshotId)
        }.also {
            appendEvent("snapshot_created", "Snapshot created: $label")
        }.toDomain()

    override suspend fun restoreLatestSnapshot(): Boolean {
        val snapshot = database.snapshotDao().getLatest() ?: return false
        val identity = json.decodeFromString<IdentityEntity>(snapshot.identityJson)
        val memories = json.decodeFromString<List<MemoryEntity>>(snapshot.memoriesJson)
        val tasks = json.decodeFromString<List<TaskEntity>>(snapshot.tasksJson)

        database.withTransaction {
            database.identityDao().deleteAll()
            database.memoryDao().deleteAll()
            database.taskDao().deleteAll()
            database.identityDao().insert(identity)
            memories.forEach { database.memoryDao().insert(it) }
            tasks.forEach { database.taskDao().insert(it) }
        }
        appendEvent("snapshot_restored", "Snapshot restored: ${snapshot.label}")
        return true
    }

    override suspend fun listSnapshots(): List<AgentSnapshot> =
        database.snapshotDao().getAll().map { it.toDomain() }

    private fun IdentityEntity.toDomain() = AgentIdentity(
        id = id,
        name = name,
        persona = persona,
        values = values,
        goals = goals,
        version = version,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun MemoryEntity.toDomain() = AgentMemory(
        id = id,
        title = title,
        content = content,
        kind = kind,
        importance = importance,
        source = source,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun TaskEntity.toDomain() = AgentTask(
        id = id,
        title = title,
        details = details,
        isComplete = isComplete,
        createdAt = createdAt,
        completedAt = completedAt,
    )

    private fun SnapshotEntity.toDomain() = AgentSnapshot(
        id = id,
        label = label,
        identityJson = identityJson,
        memoriesJson = memoriesJson,
        tasksJson = tasksJson,
        createdAt = createdAt,
    )
}
