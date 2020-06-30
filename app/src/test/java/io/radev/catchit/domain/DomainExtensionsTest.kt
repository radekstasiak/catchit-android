package io.radev.catchit.domain

import io.radev.catchit.DateTimeConverter
import io.radev.catchit.DateTimeConverterImpl
import io.radev.catchit.TestHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/*
 * Created by radoslaw on 30/06/2020.
 * radev.io 2020.
 */

class DomainExtensionsTest : TestHelper() {

    @Mock
    lateinit var dateTimeConverter: DateTimeConverterImpl

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(dateTimeConverter.getNowInMillis()).thenReturn(0L)
        Mockito.`when`(dateTimeConverter.getWaitTime(Mockito.anyLong(),Mockito.anyLong())).thenReturn("1m")
        Mockito.`when`(dateTimeConverter.convertMillisToHumanFormat(Mockito.anyLong())).thenReturn("12:57")
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
        Assert.assertEquals("42",result[0].lineName)
        Assert.assertEquals(1593517800000,result[0].timestamp)
        Assert.assertEquals("4",result[1].lineName)
        Assert.assertEquals(1593518040000,result[1].timestamp)
        Assert.assertEquals("16",result[2].lineName)
        Assert.assertEquals(1593518220000,result[2].timestamp)
        Assert.assertEquals("61",result[3].lineName)
        Assert.assertEquals(1593518220000,result[3].timestamp)
    }
}