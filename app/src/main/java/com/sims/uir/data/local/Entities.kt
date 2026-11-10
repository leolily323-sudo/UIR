package com.sims.uir.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "identities")
data class IdentityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val persona: String,
    val values: String,
    val goals: String,
    val version: Int,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val kind: String,
    val importance: Int,
    val source: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val details: String,
    val isComplete: Boolean,
    val createdAt: Long,
    val completedAt: Long?,
)

@Serializable
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventType: String,
    val message: String,
    val payload: String,
    val createdAt: Long,
)

@Serializable
@Entity(tableName = "snapshots")
data class SnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val identityJson: String,
    val memoriesJson: String,
    val tasksJson: String,
    val createdAt: Long,
)
