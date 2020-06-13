package io.radev.catchit

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.threeten.bp.ZoneId

/*
 * Created by radoslaw on 13/06/2020.
 * radev.io 2020.
 */

class DateTimeConverterTest {
    @Spy
    lateinit var converter: DateTimeConverter
    private val zoneId = ZoneId.of("GMT+1")
    private val nowInMillis = 1592052660000
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        converter = Mockito.spy(DateTimeConverter())
        Mockito.`when`(converter.getZoneId()).thenReturn(zoneId)
        Mockito.`when`(converter.getNowInMillis()).thenReturn(nowInMillis)
    }

    @Test
    fun convertStringToMillisTest() {
        val value = "2020-06-13T13:29:30+01:00"
        val result = converter.convertStringToMillis(value)
        Assert.assertEquals(1592051370000, result)
    }

    @Test
    fun convertDateAndTimeToMillisTest(){
        val date="2020-06-13"
        val time="13:55"
        val result = converter.convertDateAndTimeToMillis(date = date, time = time)
        Assert.assertEquals(1592052900000, result)
    }

    @Test
    fun getWaitingTimeTest(){
        val expectedTime = 1592052900000
        val result = converter.getWaitTime(startTime = nowInMillis, endTime = expectedTime)
        Assert.assertEquals(4,result)
    }
}