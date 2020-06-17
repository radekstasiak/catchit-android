package io.radev.catchit

import io.radev.catchit.data.DataRepository
import io.radev.catchit.data.DataRepositoryImpl
import io.radev.catchit.db.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/*
 * Created by radoslaw on 17/06/2020.
 * radev.io 2020.
 */

class DataRepositoryTest {
    @Mock
    lateinit var db: CatchItDatabase

    @Mock
    lateinit var favouriteLineDao: FavouriteLineDao

    @Mock
    lateinit var favouriteStopDao: FavouriteStopDao
    lateinit var repository: DataRepository

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        repository = DataRepositoryImpl(db = db)
        Mockito.`when`(db.favouriteLineDao()).thenReturn(favouriteLineDao)
        Mockito.`when`(db.favouriteStopDao()).thenReturn(favouriteStopDao)
    }

    @Test
    fun removeFavouriteStop_by_atcocode_test() {
        repository.removeFavouriteStopByAtcocode(atcocode = "450012351")
        Mockito.verify(favouriteStopDao).deleteByAtcocode(atcocode = "450012351")
    }

    @Test
    fun addFavouriteStop_test() {
        val entity = FavouriteStop(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450012351"
        )
        repository.addFavouriteStop(favouriteStop = entity)
        Mockito.verify(favouriteStopDao).insertAll(entity)
    }

    @Test
    fun findFavouriteLineByAtcocode_result_exists_test() {
        Mockito.`when`(db.favouriteStopDao().findByAtcocode(atcocode = "450012351")).thenReturn(
            arrayListOf(
                FavouriteStop(
                    createdAt = 1L,
                    modifiedAt = 1L,
                    atcocode = "450012351"
                )
            )
        )
        val result = repository.findFavouriteLineByAtcocode(atcocode = "450012351")
        Assert.assertEquals(1, result.size)
    }

    @Test
    fun findFavouriteLineByAtcocode_result_not_exists_test() {
        Mockito.`when`(db.favouriteStopDao().findByAtcocode(atcocode = "450012351"))
            .thenReturn(emptyList())
        val result = repository.findFavouriteLineByAtcocode(atcocode = "450012351")
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun addFavouriteLine_test() {
        val entity = FavouriteLine(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450012351",
            lineName = "51"
        )
        repository.addFavouriteLine(favouriteLine = entity)
        Mockito.verify(favouriteLineDao).insertAll(entity)
    }

    @Test
    fun removeFavouriteLine_by_atcocode_and_lineName_test() {
        repository.removeFavouriteLineByAtcocodeAndLineName(atcocode = "450012351", lineName = "51")
        Mockito.verify(favouriteLineDao)
            .deleteByAtcocodeAndLineName(atcocode = "450012351", lineName = "51")
    }


}