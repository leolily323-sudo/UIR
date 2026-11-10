package com.sims.uir.data

import androidx.room.Room
import com.sims.uir.data.local.UirDatabase
import com.sims.uir.diagnostics.DiagnosticsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class RoomAgentRepositoryTest {
    private lateinit var database: UirDatabase
    private lateinit var repository: RoomAgentRepository
    private var timestamp = 1_000L

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            UirDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = RoomAgentRepository(database, clock = { timestamp++ })
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun ensuresAndReadsActiveIdentity() = runBlocking {
        val created = repository.ensureActiveIdentity()
        val stored = database.identityDao().getActive()

        assertNotNull(stored)
        assertEquals(created.name, stored?.name)
        assertEquals("Local Identity", stored?.name)
        assertTrue(stored?.isActive == true)
    }

    @Test
    fun savesAndObservesMemory() = runBlocking {
        repository.addMemory("First memory", "Persist this locally")

        val memory = repository.observeMemories().first().single()

        assertEquals("First memory", memory.title)
        assertEquals("Persist this locally", memory.content)
    }

    @Test
    fun restoresSnapshotAndPriorMemoryCount() = runBlocking {
        repository.ensureActiveIdentity()
        repository.addMemory("Before snapshot", "Keep this memory")
        val snapshot = repository.createSnapshot("Before changes")
        repository.addMemory("After snapshot", "Remove when restored")

        assertEquals(2, repository.observeMemories().first().size)
        assertTrue(repository.restoreLatestSnapshot())

        val restored = repository.observeMemories().first()
        assertEquals(1, restored.size)
        assertEquals("Before snapshot", restored.single().title)
        assertEquals(snapshot.label, repository.listSnapshots().single().label)
    }

    @Test
    fun diagnosticsOpensDatabaseCountsRowsAndRecordsEvent() = runBlocking {
        repository.ensureActiveIdentity()
        repository.addMemory("Diagnostic memory", "count me")

        val result = DiagnosticsManager(database).runDiagnostics()

        assertTrue(result.passed)
        assertNotNull(result.recordCounts)
        assertEquals(1, result.recordCounts?.identities)
        assertEquals(1, result.recordCounts?.memories)
        assertEquals(2, result.recordCounts?.events)
    }
}
