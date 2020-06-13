package io.radev.catchit

import com.squareup.moshi.Json
import io.radev.catchit.network.DepartureDetails
import io.radev.catchit.network.DepartureStatus
import io.radev.catchit.network.toSingleBusNotificationModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.threeten.bp.ZoneId

/*
 * Created by radoslaw on 13/06/2020.
 * radev.io 2020.
 */

class SingleBusNotificationModelTest {

    @Mock
    lateinit var converter: DateTimeConverter

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(converter.getZoneId()).thenReturn(ZoneId.of("GMT+1"))
        Mockito.`when`(converter.getNowInMillis()).thenReturn(1592052660000)
        Mockito.`when`(converter.convertDateAndTimeToMillis(date = Mockito.anyString(), time = Mockito.anyString())).thenReturn(1592052900000)

    }
    @Test
    fun departureToSingleBusNotificationModelTest() {
        val departure = DepartureDetails(
            mode = "bus",
            line = "51",
            lineName = "51",
            direction = "Morley Town Hall",
            operator = "FLDS",
            date="2020-06-13",
            expectedDepartureDate = "2020-06-13",
            aimedDepartureTime = "13:55",
            expectedDepartureTime = "13:55",
            bestDepartureEstimate = "13:55",
            status = mapOf("cancellation" to DepartureStatus(value=false, reason = null)),
            source = "FirstTicketer",
            dir = "outbound",
            operatorName = "First Leeds",
            id = "https://transportapi.com/v3/uk/bus/route/FLDS/51/outbound/450010441/2020-06-13/13:55/timetable.json?app_id=68755067\\u0026app_key=1f81945ff77187126de7f9f93c5fab44"
        )
        Mockito.`when`(converter.getWaitTime(startTime = 1592052660000,endTime = 1592052900000)).thenReturn(4L)
        val result = departure.toSingleBusNotificationModel(dateTimeConverter = converter)
        Assert.assertTrue(result is SingleBusNotificationModel)
        Assert.assertTrue(result.line == "51")
        Assert.assertTrue(result.direction == "Morley Town Hall")
        Assert.assertTrue(result.waitTime == "4m")
    }

    @Test
    fun departureToSingleBusNotificationModelTest_waitTimeIsDue() {
        val departure = DepartureDetails(
            mode = "bus",
            line = "51",
            lineName = "51",
            direction = "Morley Town Hall",
            operator = "FLDS",
            date="2020-06-13",
            expectedDepartureDate = "2020-06-13",
            aimedDepartureTime = "13:55",
            expectedDepartureTime = "13:55",
            bestDepartureEstimate = "13:55",
            status = mapOf("cancellation" to DepartureStatus(value=false, reason = null)),
            source = "FirstTicketer",
            dir = "outbound",
            operatorName = "First Leeds",
            id = "https://transportapi.com/v3/uk/bus/route/FLDS/51/outbound/450010441/2020-06-13/13:55/timetable.json?app_id=68755067\\u0026app_key=1f81945ff77187126de7f9f93c5fab44"
        )
        Mockito.`when`(converter.getWaitTime(startTime = 1592052660000,endTime = 1592052900000)).thenReturn(0)
        val result = departure.toSingleBusNotificationModel(dateTimeConverter = converter)
        Assert.assertTrue(result is SingleBusNotificationModel)
        Assert.assertTrue(result.line == "51")
        Assert.assertTrue(result.direction == "Morley Town Hall")
        Assert.assertTrue(result.waitTime == "DUE")
    }

    @Test
    fun departureToSingleBusNotificationModelTest_expectedDepartureDateIsNull() {
        val departure = DepartureDetails(
            mode = "bus",
            line = "51",
            lineName = "51",
            direction = "Morley Town Hall",
            operator = "FLDS",
            date="2020-06-13",
            expectedDepartureDate = null,
            aimedDepartureTime = "13:55",
            expectedDepartureTime = "13:55",
            bestDepartureEstimate = "13:55",
            status = mapOf("cancellation" to DepartureStatus(value=false, reason = null)),
            source = "FirstTicketer",
            dir = "outbound",
            operatorName = "First Leeds",
            id = "https://transportapi.com/v3/uk/bus/route/FLDS/51/outbound/450010441/2020-06-13/13:55/timetable.json?app_id=68755067\\u0026app_key=1f81945ff77187126de7f9f93c5fab44"
        )
        Mockito.`when`(converter.getWaitTime(startTime = 1592052660000,endTime = 1592052900000)).thenReturn(4L)
        val result = departure.toSingleBusNotificationModel(dateTimeConverter = converter)
        Assert.assertTrue(result is SingleBusNotificationModel)
        Assert.assertTrue(result.line == "51")
        Assert.assertTrue(result.direction == "Morley Town Hall")
        Assert.assertTrue(result.waitTime == "4m")
    }

    @Test
    fun departureToSingleBusNotificationModelTest_expectedDepartureTimeIsNull() {
        val departure = DepartureDetails(
            mode = "bus",
            line = "51",
            lineName = "51",
            direction = "Morley Town Hall",
            operator = "FLDS",
            date="2020-06-13",
            expectedDepartureDate = "2020-06-13",
            aimedDepartureTime = "13:55",
            expectedDepartureTime = null,
            bestDepartureEstimate = "13:55",
            status = mapOf("cancellation" to DepartureStatus(value=false, reason = null)),
            source = "FirstTicketer",
            dir = "outbound",
            operatorName = "First Leeds",
            id = "https://transportapi.com/v3/uk/bus/route/FLDS/51/outbound/450010441/2020-06-13/13:55/timetable.json?app_id=68755067\\u0026app_key=1f81945ff77187126de7f9f93c5fab44"
        )
        Mockito.`when`(converter.getWaitTime(startTime = 1592052660000,endTime = 1592052900000)).thenReturn(4L)
        val result = departure.toSingleBusNotificationModel(dateTimeConverter = converter)
        Assert.assertTrue(result is SingleBusNotificationModel)
        Assert.assertTrue(result.line == "51")
        Assert.assertTrue(result.direction == "Morley Town Hall")
        Assert.assertTrue(result.waitTime == "4m")
    }
}
