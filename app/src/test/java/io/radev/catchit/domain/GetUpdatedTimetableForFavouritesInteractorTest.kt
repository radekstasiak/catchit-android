package io.radev.catchit.domain

import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.radev.catchit.DateTimeConverter
import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.DepartureResponse
import io.radev.catchit.network.ErrorResponse
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.network.toDomainModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * Created by radoslaw on 29/06/2020.
 * radev.io 2020.
 */


class GetUpdatedTimetableForFavouritesInteractorTest {

    @RelaxedMockK
    lateinit var dataRepository: DataRepository

    @RelaxedMockK
    lateinit var dateTimeConverter: DateTimeConverter

    lateinit var interactor: UpdateFavouriteDeparturesAlertInteractor

    @Before
    fun before() {
        MockKAnnotations.init(this)
        mockkStatic("io.radev.catchit.network.NetworkKt")
        interactor = UpdateFavouriteDeparturesAlertInteractor(
            dataRepository = dataRepository,
            dateTimeConverter = dateTimeConverter
        )
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

    @Test
    fun `when getDeparturesList called it returns correct updated departures state`() {
        coEvery { dataRepository.getAllFavouriteLinesSync() } returns listOf(
            mockk {
                every { atcocode } returns "450010441"
            },
            mockk {
                every { atcocode } returns "450010361"
            },
            mockk {
                every { atcocode } returns "450010441"
            },
            mockk {
                every { atcocode } returns "450010467"
            },
            mockk {
                every { atcocode } returns "450010267"
            },
            mockk {
                every { atcocode } returns "450010667"
            }
        )

        coEvery { dataRepository.getLiveTimetable(any()) } returnsMany listOf(
            mockk<NetworkResponse.Success<DepartureResponse>>(relaxed = true)
            {
                every { body } returns mockk {
                    every { toDomainModel(dateTimeConverter) } returns mockk {}
                }
            },
            mockk<NetworkResponse.ApiError<ErrorResponse>>(relaxed = true)
            {
                every { code } returns 500
            },
            mockk<NetworkResponse.NetworkError>(relaxed = true),
            mockk<NetworkResponse.Success<DepartureResponse>>(relaxed = true)
            {
                every { body } returns mockk {
                    every { toDomainModel(dateTimeConverter) } returns mockk {}
                }
            },
            mockk<NetworkResponse.UnknownError>(relaxed = true)
            {
                every { error } returns Throwable("unknown")
            }
        )

        runBlocking {
            val result = interactor.getFavouriteDeparturesUpdate()
            Assert.assertEquals(5, result.size)
            Assert.assertTrue(result[0] is FavouriteDepartureUpdateState.Success)
            Assert.assertTrue(result[1] is FavouriteDepartureUpdateState.ApiError)
            Assert.assertTrue(result[2] is FavouriteDepartureUpdateState.NetworkError)
            Assert.assertTrue(result[3] is FavouriteDepartureUpdateState.Success)
            Assert.assertTrue(result[4] is FavouriteDepartureUpdateState.UnknownError)
        }

    }

//    @Test
//    fun `filterFavouriteLines returns list of favourite departures alert`(){
//
//
//    }

}