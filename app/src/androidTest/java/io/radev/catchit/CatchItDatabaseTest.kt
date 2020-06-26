package io.radev.catchit

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.radev.catchit.db.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

/*
 * Created by radoslaw on 15/06/2020.
 * radev.io 2020.
 */

@RunWith(AndroidJUnit4::class)
class CatchItDatabaseTest {
    private lateinit var favouriteStopDao: FavouriteStopDao
    private lateinit var favouriteLineDao: FavouriteLineDao
    private lateinit var db: CatchItDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CatchItDatabase::class.java
        ).build()
        favouriteLineDao = db.favouriteLineDao()
        favouriteStopDao = db.favouriteStopDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun populatedDb() = runBlocking {
        val entity1 = FavouriteLine(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450012351",
            lineName = "51"
        )

        val entity2 = FavouriteLine(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450010439",
            lineName = "52"
        )

        db.favouriteLineDao().insertAll(entity1, entity2)

        val favouriteStop = FavouriteStop(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450012351"
        )

        val favouriteStop2 = FavouriteStop(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450010439"
        )
        db.favouriteStopDao().insertAll(favouriteStop, favouriteStop2)
    }

    //FavouriteLineDao test
    @Test
    @Throws(Exception::class)
    fun writeFavouriteLineAndReadList() = runBlocking {
        populatedDb()
        val result = db.favouriteLineDao().getAll().getOrAwaitValue()
        Assert.assertEquals(2, result.size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteFavouriteLine_test() = runBlocking {
        populatedDb()
        val result = db.favouriteLineDao().getAll().getOrAwaitValue()
        db.favouriteLineDao().delete(result[0])
        val updatedResult = db.favouriteLineDao().getAll().getOrAwaitValue()
        Assert.assertEquals(1, updatedResult.size)
    }

    @Test
    @Throws(Exception::class)
    fun findByAtcocodeAndLine_no_result_test_test() = runBlocking {
        populatedDb()
        val result = db.favouriteLineDao().findByAtcocodeAndLine("4500104319", "52")
        Assert.assertEquals(null, result)
    }

    @Test
    @Throws(Exception::class)
    fun findByAtcocodeAndLine_result_exist_test() = runBlocking {
        populatedDb()
        val result = db.favouriteLineDao().findByAtcocodeAndLine("450010439", "52")
        Assert.assertEquals(result.atcocode, "450010439")
    }

    @Test
    @Throws(Exception::class)
    fun deleteFavouriteLine_with_atcocode_and_lineName_test() = runBlocking {
        populatedDb()
        val result = db.favouriteLineDao().getAll().getOrAwaitValue()
        Assert.assertEquals(2, result.size)
        db.favouriteLineDao().deleteByAtcocodeAndLineName(atcocode = "450012351", lineName = "51")
        val updatedResult = db.favouriteLineDao().getAll().getOrAwaitValue()
        Assert.assertEquals(1, updatedResult.size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteFavouriteLine_with_atcocode_and_lineName_not_exists_test() = runBlocking {
        populatedDb()
        val result  = db.favouriteLineDao().getAll().getOrAwaitValue()
        Assert.assertEquals(2, result.size)
        db.favouriteLineDao().deleteByAtcocodeAndLineName(atcocode = "450012351", lineName = "52")
        val updatedResult  = db.favouriteLineDao().getAll().getOrAwaitValue()
        Assert.assertEquals(2,updatedResult.size)
    }

    //FavouriteStopDao test
    @Test
    @Throws(Exception::class)
    fun writeFavouriteStopAndReadList() = runBlocking {
        populatedDb()
        val result = db.favouriteStopDao().getAll().getOrAwaitValue()
        Assert.assertEquals(2, result.size)
    }

    @Test
    @Throws(Exception::class)
    fun writeFavouriteStop_duplicateAtcocode_test() = runBlocking {
        populatedDb()
        val favouriteStop = FavouriteStop(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450012351"
        )
        db.favouriteStopDao().insertAll(favouriteStop)
        val result = db.favouriteStopDao().getAll().getOrAwaitValue()
        Assert.assertEquals(2, result.size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteFavouriteStop_test() = runBlocking {
        populatedDb()
        val result = db.favouriteStopDao().getAll().getOrAwaitValue()
        db.favouriteStopDao().delete(result[0])
        val updatedResult = db.favouriteStopDao().getAll().getOrAwaitValue()
        Assert.assertEquals(1, updatedResult.size)
    }

    @Test
    @Throws(Exception::class)
    fun findByAtcocode_no_result_test_test() = runBlocking {
        populatedDb()
        val result = db.favouriteStopDao().findByAtcocode("4500104319")
        Assert.assertEquals(0, result.size)
    }

    @Test
    @Throws(Exception::class)
    fun findByAtcocode_result_exist_test() = runBlocking {
        populatedDb()
        val result = db.favouriteStopDao().findByAtcocode("450010439")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("450010439", result[0].atcocode)
    }

    @Test
    @Throws(Exception::class)
    fun deleteFavouriteStop_withAtcocode_test() = runBlocking {
        populatedDb()
        val result =  db.favouriteStopDao().getAll().getOrAwaitValue()
        Assert.assertEquals(2,result.size)
        db.favouriteStopDao().deleteByAtcocode(atcocode = "450012351")
        val updatedResult =  db.favouriteStopDao().getAll().getOrAwaitValue()
        Assert.assertEquals(1, updatedResult.size)
    }


}
