package com.atheer.core.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.atheer.core.database.AtheerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class OfflineFirstVideoRepositoryTest {

    private lateinit var database: AtheerDatabase
    private lateinit var repository: OfflineFirstVideoRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AtheerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = OfflineFirstVideoRepository(
            videoDao = database.videoDao(),
            commentDao = database.commentDao(),
            timeSource = object : TimeSource {
                override fun now(): Long = 1_700_000_000_000L
            },
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `seeds demo content exactly once`() = runTest {
        assertTrue(repository.observeAllVideos().first().isEmpty())

        repository.seedIfEmpty()
        val firstRun = repository.observeAllVideos().first()
        assertTrue(firstRun.isNotEmpty())

        // Second call is a no-op (idempotent bootstrap).
        repository.seedIfEmpty()
        val afterSecondCall = repository.observeAllVideos().first()
        assertEquals(firstRun.size, afterSecondCall.size)
    }

    @Test
    fun `setLiked toggles count exactly once - no double counting`() = runTest {
        repository.seedIfEmpty()
        val video = repository.observeAllVideos().first().first()
        val baselineLikes = video.likes

        repository.setLiked(video.id, isLiked = true)
        val liked = repository.observeVideo(video.id).first()!!
        assertEquals(baselineLikes + 1, liked.likes)
        assertTrue(liked.isLiked)

        repository.setLiked(video.id, isLiked = false)
        val unliked = repository.observeVideo(video.id).first()!!
        assertEquals(baselineLikes, unliked.likes)
        assertFalse(unliked.isLiked)
    }

    @Test
    fun `setSubscribed updates every video of the same channel`() = runTest {
        repository.seedIfEmpty()
        val video = repository.observeAllVideos().first().first { !it.isSubscribed }
        val sameChannelOthers = repository.observeAllVideos().first()
            .filter { it.channelName == video.channelName && it.id != video.id }

        // Ensure the fixture has a multi-video channel; otherwise skip semantics check.
        repository.setSubscribed(video.id, isSubscribed = true)

        val updated = repository.observeAllVideos().first()
            .filter { it.channelName == video.channelName }
        assertTrue(updated.all { it.isSubscribed })
        assertEquals(sameChannelOthers.size + 1, updated.size)
    }

    @Test
    fun `addComment persists and observes`() = runTest {
        repository.seedIfEmpty()
        val video = repository.observeAllVideos().first().first()
        val before = repository.observeComments(video.id).first().size

        repository.addComment(video.id, author = "tester", text = "hello")

        val after = repository.observeComments(video.id).first()
        assertEquals(before + 1, after.size)
        assertEquals("hello", after.last().text)
    }

    @Test
    fun `incrementViews bumps counter by one`() = runTest {
        repository.seedIfEmpty()
        val video = repository.observeAllVideos().first().first()
        val baseline = video.views

        repository.incrementViews(video.id)

        assertEquals(baseline + 1, repository.getVideo(video.id)!!.views)
    }
}
