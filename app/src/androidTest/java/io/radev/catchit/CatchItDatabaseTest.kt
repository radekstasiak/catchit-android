package io.radev.catchit

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.radev.catchit.db.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
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

    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CatchItDatabase::class.java).build()
        favouriteLineDao = db.favouriteLineDao()
        favouriteStopDao = db.favouriteStopDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
    //FavouriteLineDao test
    @Test
    @Throws(Exception::class)
    fun writeFavouriteLineAndReadList() {
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
        db.favouriteLineDao().insertAll(entity1,entity2)
        val result = db.favouriteLineDao().getAll()
        Assert.assertEquals(2, result.size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteFavouriteLine_test() {
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
        db.favouriteLineDao().insertAll(entity1,entity2)
        val result = db.favouriteLineDao().getAll()
        db.favouriteLineDao().delete(result[0])
        val updatedResult = db.favouriteLineDao().getAll()
        Assert.assertEquals(1, updatedResult.size)
    }

    @Test
    @Throws(Exception::class)
    fun findByAtcocodeAndLine_no_result_test_test() {
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
        db.favouriteLineDao().insertAll(entity1,entity2)
        val result = db.favouriteLineDao().findByAtcocodeAndLine("4500104319","52")
        Assert.assertEquals(null, result)
    }

    @Test
    @Throws(Exception::class)
    fun findByAtcocodeAndLine_result_exist_test() {
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
        db.favouriteLineDao().insertAll(entity1,entity2)
        val result = db.favouriteLineDao().findByAtcocodeAndLine("450010439","52")
        Assert.assertEquals(result.atcocode,"450010439")
    }

    //FavouriteStopDao test
    @Test
    @Throws(Exception::class)
    fun writeFavouriteStopAndReadList() {
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
        db.favouriteStopDao().insertAll(favouriteStop,favouriteStop2)
        val result = db.favouriteStopDao().getAll()
        Assert.assertEquals(2, result.size)
    }

    @Test
    @Throws(Exception::class)
    fun writeFavourietStop_duplicateAtcocode_test() {
        val favouriteStop = FavouriteStop(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450012351"
        )

        val favouriteStop2 = FavouriteStop(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450012351"
        )
        db.favouriteStopDao().insertAll(favouriteStop,favouriteStop2)
        val result = db.favouriteStopDao().getAll()
        Assert.assertEquals(1, result.size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteFavouriteStop_test() {
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
        db.favouriteStopDao().insertAll(favouriteStop,favouriteStop2)
        val result = db.favouriteStopDao().getAll()
        db.favouriteStopDao().delete(result[0])
        val updatedResult = db.favouriteStopDao().getAll()
        Assert.assertEquals(1, updatedResult.size)
    }

    @Test
    @Throws(Exception::class)
    fun findByAtcocode_no_result_test_test() {
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
        db.favouriteStopDao().insertAll(favouriteStop,favouriteStop2)
        val result = db.favouriteStopDao().findByAtcocode("4500104319")
        Assert.assertEquals(null, result)
    }

    @Test
    @Throws(Exception::class)
    fun findByAtcocode_result_exist_test() {
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
        db.favouriteStopDao().insertAll(favouriteStop,favouriteStop2)
        val result = db.favouriteStopDao().findByAtcocode("450010439")
        Assert.assertEquals(result.atcocode,"450010439")
    }


}

class TestUtil{

}