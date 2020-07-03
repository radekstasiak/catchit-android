package io.radev.catchit.domain

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.radev.catchit.DateTimeConverter
import io.radev.catchit.TestHelper
import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.DepartureResponse
import io.radev.catchit.network.ErrorResponse
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.network.toDomainModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException


/*
 * Created by radoslaw on 30/06/2020.
 * radev.io 2020.
 */

class GetDeparturesInteractorTest : TestHelper() {

    lateinit var interactor: GetDeparturesInteractor

    @MockK
    lateinit var dataRepository: DataRepository

    @MockK
    lateinit var dateTimeConverter: DateTimeConverter

    @MockK
    lateinit var departureResponseMock: DepartureResponse

    @MockK
    lateinit var domainModelMock: DepartureDomainModel

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic("io.radev.catchit.network.NetworkKt")
        interactor = GetDeparturesInteractor(
            dataRepository = dataRepository,
            dateTimeConverter = dateTimeConverter
        )
    }

    @Test
    fun getDeparturesState_returns_success_test() = runBlocking {
        coEvery { dataRepository.getLiveTimetable(atcocode = "450011104") } returns NetworkResponse.Success(
            body = departureResponseMock
        )
        every { departureResponseMock.toDomainModel(dateTimeConverter = dateTimeConverter) } returns domainModelMock
        val result = interactor.getDepartureState(atcocode = "450011104")
        Assert.assertTrue(result is DeparturesState.Success)
        Assert.assertEquals(domainModelMock, (result as DeparturesState.Success).data)
    }

    @Test
    fun getDepartureState_returns_apiError_test() = runBlocking {
        coEvery { dataRepository.getLiveTimetable(atcocode = "450011104") } returns NetworkResponse.ApiError<ErrorResponse>(
            body = ErrorResponse(error = 500, message = "error"), code = 500
        )

        val result = interactor.getDepartureState(atcocode = "450011104")
        Assert.assertTrue(result is DeparturesState.ApiError)
        Assert.assertEquals(500, (result as DeparturesState.ApiError).code)
    }

    @Test
    fun getDepartureState_returns_networkError_test() = runBlocking {
        coEvery { dataRepository.getLiveTimetable(atcocode = "450011104") } returns NetworkResponse.NetworkError(error = IOException())

        val result = interactor.getDepartureState(atcocode = "450011104")
        Assert.assertTrue(result is DeparturesState.NetworkError)
    }

    @Test
    fun getDepartureState_returns_unknownError_test() = runBlocking {
        coEvery { dataRepository.getLiveTimetable(atcocode = "450011104") } returns NetworkResponse.UnknownError(error = Throwable())

        val result = interactor.getDepartureState(atcocode = "450011104")
        Assert.assertTrue(result is DeparturesState.UnknownError)
    }


}
