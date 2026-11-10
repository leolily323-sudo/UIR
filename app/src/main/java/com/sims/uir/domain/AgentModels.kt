package com.sims.uir.domain

data class AgentIdentity(
    val id: Long,
    val name: String,
    val persona: String,
    val values: String,
    val goals: String,
    val version: Int,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

data class AgentMemory(
    val id: Long,
    val title: String,
    val content: String,
    val kind: String,
    val importance: Int,
    val source: String,
    val createdAt: Long,
    val updatedAt: Long,
)

data class AgentTask(
    val id: Long,
    val title: String,
    val details: String,
    val isComplete: Boolean,
    val createdAt: Long,
    val completedAt: Long?,
)

data class AgentSnapshot(
    val id: Long,
    val label: String,
    val identityJson: String,
    val memoriesJson: String,
    val tasksJson: String,
    val createdAt: Long,
)
