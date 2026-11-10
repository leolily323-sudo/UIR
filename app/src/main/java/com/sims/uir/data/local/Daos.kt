package com.sims.uir.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IdentityDao {
    @Query("SELECT * FROM identities WHERE isActive = 1 ORDER BY id LIMIT 1")
    suspend fun getActive(): IdentityEntity?

    @Query("SELECT * FROM identities WHERE isActive = 1 ORDER BY id LIMIT 1")
    fun observeActive(): Flow<IdentityEntity?>

    @Query("SELECT COUNT(*) FROM identities")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(identity: IdentityEntity): Long

    @Query("DELETE FROM identities")
    suspend fun deleteAll()
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY createdAt DESC, id DESC")
    fun observeAll(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories ORDER BY createdAt DESC, id DESC")
    suspend fun getAll(): List<MemoryEntity>

    @Query("SELECT COUNT(*) FROM memories")
    suspend fun count(): Int

    @Insert
    suspend fun insert(memory: MemoryEntity): Long

    @Query("DELETE FROM memories")
    suspend fun deleteAll()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC, id DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC, id DESC")
    suspend fun getAll(): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int

    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Query("UPDATE tasks SET isComplete = 1, completedAt = :completedAt WHERE id = :taskId")
    suspend fun complete(taskId: Long, completedAt: Long): Int

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY createdAt DESC, id DESC")
    fun observeAll(): Flow<List<EventEntity>>

    @Query("SELECT COUNT(*) FROM events")
    suspend fun count(): Int

    @Insert
    suspend fun insert(event: EventEntity): Long
}

@Dao
interface SnapshotDao {
    @Query("SELECT * FROM snapshots ORDER BY createdAt DESC, id DESC")
    fun observeAll(): Flow<List<SnapshotEntity>>

    @Query("SELECT * FROM snapshots ORDER BY createdAt DESC, id DESC")
    suspend fun getAll(): List<SnapshotEntity>

    @Query("SELECT * FROM snapshots ORDER BY createdAt DESC, id DESC LIMIT 1")
    suspend fun getLatest(): SnapshotEntity?

    @Query("SELECT * FROM snapshots WHERE id = :snapshotId")
    suspend fun getById(snapshotId: Long): SnapshotEntity?

    @Query("SELECT COUNT(*) FROM snapshots")
    suspend fun count(): Int

    @Insert
    suspend fun insert(snapshot: SnapshotEntity): Long
}
