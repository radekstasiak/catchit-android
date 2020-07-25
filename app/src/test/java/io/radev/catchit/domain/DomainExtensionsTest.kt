package io.radev.catchit.domain

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.radev.catchit.DateTimeConverterImpl
import io.radev.catchit.TestHelper
import io.radev.catchit.data.DataRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * Created by radoslaw on 30/06/2020.
 * radev.io 2020.
 */

class DomainExtensionsTest : TestHelper() {

    @RelaxedMockK
    lateinit var dateTimeConverter: DateTimeConverterImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        every { dateTimeConverter.getNowInMillis() } returns 0L
        every { dateTimeConverter.getWaitTime(any(), any()) } returns "1m"
        every { dateTimeConverter.convertMillisToHumanFormat(any()) } returns "12:57"
    }

    @Test
    fun `test mapping between DepartureDetailsDomainModel and DepartureDetailsUiModel returns results with unique pair of line name and direction`() {
        val domainModel = getDepartureDomainModel()
        val result =
            domainModel.departures.toDepartureDetailsUiModel(
                atcocode = "450011104",
                dateTimeConverter = dateTimeConverter
            )
        Assert.assertTrue(result.size == 4)
    }

    @Test
    fun `test mapping between DepartureDetailsDomainModel and DepartureDetailsUiModel returns unique results with best estimate times`() {
        val domainModel = getDepartureDomainModel()
        val result =
            domainModel.departures.toDepartureDetailsUiModel(
                atcocode = "450011104",
                dateTimeConverter = dateTimeConverter
            )
        Assert.assertTrue(result.size == 4)
        Assert.assertEquals("42", result[0].lineName)
        Assert.assertEquals(1593517800000, result[0].timestamp)
        Assert.assertEquals("4", result[1].lineName)
        Assert.assertEquals(1593518040000, result[1].timestamp)
        Assert.assertEquals("16", result[2].lineName)
        Assert.assertEquals(1593518220000, result[2].timestamp)
        Assert.assertEquals("61", result[3].lineName)
        Assert.assertEquals(1593518220000, result[3].timestamp)
    }

    @Test
    fun `test create list of FavouriteDepartureAlert from DepartureDetailsDomainModel and FavouriteLine list`() =
        runBlocking {
            val departureDomainModel = getDepartureDomainModel()

            val dataRepositoryMock = mockk<DataRepository>(relaxed = true)
            coEvery {
                dataRepositoryMock.getFavouriteLineByAtcocodeAndLineName(
                    "450013965",
                    "4"
                )
            } returns listOf(
                mockk {}
            )

            coEvery {
                dataRepositoryMock.getFavouriteLineByAtcocodeAndLineName(
                    "450013965",
                    "61"
                )
            } returns listOf(
                mockk {}
            )

            coEvery {
                dataRepositoryMock.getFavouriteLineByAtcocodeAndLineName(
                    "450013965",
                    "42"
                )
            } returns listOf(
                mockk {}
            )

            coEvery {
                dataRepositoryMock.getFavouriteLineByAtcocodeAndLineName(
                    "450013965",
                    "16"
                )
            } returns emptyList()

            val result = departureDomainModel.toFavouriteDepartureAlert(
                dataRepository = dataRepositoryMock,
                dateTimeConverter = dateTimeConverter
            )
            departureDomainModel.departures.sortedBy { it.bestDepartureEstimate }

            Assert.assertEquals(3, result.size)

            Assert.assertEquals("450013965", result[0].atcocode)
            Assert.assertEquals("42", result[0].lineName)
            Assert.assertEquals("Compton Road Compton Centre", result[0].stopName)
            Assert.assertEquals(1593517800000, result[0].timestamp)

            Assert.assertEquals("450013965", result[1].atcocode)
            Assert.assertEquals("4", result[1].lineName)
            Assert.assertEquals("Compton Road Compton Centre", result[1].stopName)
            Assert.assertEquals(1593518040000, result[1].timestamp)

            Assert.assertEquals("450013965", result[2].atcocode)
            Assert.assertEquals("61", result[2].lineName)
            Assert.assertEquals("Compton Road Compton Centre", result[2].stopName)
            Assert.assertEquals(1593518220000, result[2].timestamp)


        }
}