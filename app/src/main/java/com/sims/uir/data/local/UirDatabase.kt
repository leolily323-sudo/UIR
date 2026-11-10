package com.sims.uir.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        IdentityEntity::class,
        MemoryEntity::class,
        TaskEntity::class,
        EventEntity::class,
        SnapshotEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class UirDatabase : RoomDatabase() {
    abstract fun identityDao(): IdentityDao
    abstract fun memoryDao(): MemoryDao
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun snapshotDao(): SnapshotDao
}
