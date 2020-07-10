package io.radev.catchit.domain

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.DepartureResponse
import io.radev.catchit.network.NetworkResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * Created by radoslaw on 29/06/2020.
 * radev.io 2020.
 */


class GetUpdatedTimetableForFavouritesInteractorTest {

    @MockK
    lateinit var dataRepository: DataRepository

    lateinit var interactor: GetUpdatedTimetableForFavouritesInteractor

    @Before
    fun before() {
        MockKAnnotations.init(this)
        interactor = GetUpdatedTimetableForFavouritesInteractor(dataRepository = dataRepository)
    }


    @Test
    fun getNextDepartures_returns_success_results_test() {
        val successResponse = mockk<NetworkResponse.Success<DepartureResponse>>(relaxed = true)
        val networkErrorResponse = mockk<NetworkResponse.NetworkError>(relaxed = true)

        every { successResponse.body } returns
                mockk {
                    every { atcocode } returns ""
                }

        val params =
            listOf("450012351", "450012352", "450012353", "450012354", "450012355")
        coEvery { dataRepository.getLiveTimetable("450012351") } returns successResponse
        coEvery { dataRepository.getLiveTimetable("450012352") } returns successResponse
        coEvery { dataRepository.getLiveTimetable("450012353") } returns successResponse
        coEvery { dataRepository.getLiveTimetable("450012354") } returns networkErrorResponse
        coEvery { dataRepository.getLiveTimetable("450012355") } returns successResponse
        runBlocking {
            val result = interactor.getNextDepartures(params)
            Assert.assertTrue(result.size == 4)
        }

    }
    
}