package io.radev.catchit.domain

import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.DepartureResponse
import io.radev.catchit.network.NetworkResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.IOException

/*
 * Created by radoslaw on 29/06/2020.
 * radev.io 2020.
 */


class GetUpdatedTimetableForFavouritesInteractorTest {

    @Mock
    lateinit var dataRepository: DataRepository

    lateinit var interactor: GetUpdatedTimetableForFavouritesInteractor

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        interactor = GetUpdatedTimetableForFavouritesInteractor(dataRepository = dataRepository)
    }

    @Test
    fun getNextDepartures_returns_success_results_test() = runBlocking {
        val params = listOf("450012351", "450012352", "450012353", "450012354", "450012355")
        Mockito.`when`(dataRepository.getLiveTimetable("450012351"))
            .thenReturn(NetworkResponse.Success(body = getDummyDepartureRsponse(atcocode = "450012351")))
        Mockito.`when`(dataRepository.getLiveTimetable("450012352"))
            .thenReturn(NetworkResponse.Success(body = getDummyDepartureRsponse(atcocode = "450012352")))
        Mockito.`when`(dataRepository.getLiveTimetable("450012353"))
            .thenReturn(NetworkResponse.Success(body = getDummyDepartureRsponse(atcocode = "450012353")))
        Mockito.`when`(dataRepository.getLiveTimetable("450012354"))
            .thenReturn(NetworkResponse.NetworkError(error = IOException()))
        Mockito.`when`(dataRepository.getLiveTimetable("450012355"))
            .thenReturn(NetworkResponse.Success(body = getDummyDepartureRsponse(atcocode = "450012355")))
        val result = interactor.getNextDepartures(params)
        Assert.assertTrue(result.size == 4)
    }

    private fun getDummyDepartureRsponse(atcocode: String): DepartureResponse = DepartureResponse(
        atcocode = atcocode,
        smsCode = null,
        requestTime = null,
        name = null,
        stopName = null,
        bearing = null,
        indicator = null,
        location = null,
        locality = null,
        departures = null
    )
}