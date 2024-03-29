package io.radev.catchit

import io.radev.catchit.data.DataRepository
import io.radev.catchit.data.DataRepositoryImpl
import io.radev.catchit.db.*
import io.radev.catchit.network.ApiService
import kotlinx.coroutines.runBlocking
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

    @Mock
    lateinit var apiService: ApiService

    lateinit var repository: DataRepository

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        repository = DataRepositoryImpl(db = db, apiService = apiService)
        Mockito.`when`(db.favouriteLineDao()).thenReturn(favouriteLineDao)
        Mockito.`when`(db.favouriteStopDao()).thenReturn(favouriteStopDao)
    }


    @Test
    fun removeFavouriteStop_by_atcocode_test() = runBlocking {
        repository.removeFavouriteStopByAtcocode(atcocode = "450012351")
        Mockito.verify(favouriteStopDao).deleteByAtcocode(atcocode = "450012351")
    }

    @Test
    fun addFavouriteStop_test() = runBlocking {
        val entity = FavouriteStop(
            createdAt = 1L,
            modifiedAt = 1L,
            atcocode = "450012351"
        )
        repository.addFavouriteStop(favouriteStop = entity)
        Mockito.verify(favouriteStopDao).insertAll(entity)
    }

    @Test
    fun addFavouriteLine_test() = runBlocking {
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
    fun findFavouriteLineByAtcocode_result_exists_test() = runBlocking {
        Mockito.`when`(db.favouriteStopDao().findByAtcocode(atcocode = "450012351")).thenReturn(
            arrayListOf(
                FavouriteStop(
                    createdAt = 1L,
                    modifiedAt = 1L,
                    atcocode = "450012351"
                )
            )
        )
        val result = repository.findFavouriteStopByAtcocode(atcocode = "450012351")
        Assert.assertEquals(1, result.size)
    }

    @Test
    fun findFavouriteStopByAtcocode_result_not_exists_test() = runBlocking {
        Mockito.`when`(db.favouriteStopDao().findByAtcocode(atcocode = "450012351"))
            .thenReturn(emptyList())
        val result = repository.findFavouriteStopByAtcocode(atcocode = "450012351")
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun removeFavouriteLine_by_atcocode_and_lineName_test() = runBlocking {
        repository.removeFavouriteLineByAtcocodeAndLineName(atcocode = "450012351", lineName = "51")
        Mockito.verify(favouriteLineDao)
            .deleteByAtcocodeAndLineName(atcocode = "450012351", lineName = "51")
    }

    @Test
    fun getAllFavouriteStops_test() {
        repository.getAllFavouriteStops()
        Mockito.verify(favouriteStopDao).getAll()
    }

    @Test
    fun getAllFavouriteLines_test() {
        repository.getAllFavouriteLines()
        Mockito.verify(favouriteLineDao).getAll()
    }


    @Test
    fun `get favourite lines by atcocode test`() {
        runBlocking {
            repository.getFavouriteLinesByAtcocode(atcocode = "450012351")
            Mockito.verify(favouriteLineDao).getByAtcocode(atcocode = "450012351")
        }
    }

    @Test
    fun `get favourite lines by atcocode and line name test`() {
        runBlocking {
            repository.getFavouriteLineByAtcocodeAndLineName(atcocode = "450012351", lineName = "52")
            Mockito.verify(favouriteLineDao).findByAtcocodeAndLine(atcocode = "450012351", lineName = "52")
        }
    }

    @Test
    fun getPostCodeDetails_test() {
        runBlocking {
            repository.getPostCodeDetails(postCode = "LS71HT")
            Mockito.verify(apiService).getPostCodeDetails(query = "LS71HT")
        }

    }

    @Test
    fun getLiveTimetable_test() {
        runBlocking {
            repository.getLiveTimetable(atcocode = "450012351")
            Mockito.verify(apiService).getLiveTimetable(atcocode = "450012351")
        }

    }

    @Test
    fun getNearbyPlaces_test() {
        runBlocking {
            repository.getNearbyPlaces(latitude = 53.8288722, longitude = -1.5729408)
            Mockito.verify(apiService).getNearbyPlaces(lat = 53.8288722, lon = -1.5729408)
        }
    }


}