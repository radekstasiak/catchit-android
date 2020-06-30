package io.radev.catchit

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

/*
 * Created by radoslaw on 13/06/2020.
 * radev.io 2020.
 */

class DateTimeConverterImpl @Inject constructor() : DateTimeConverter {

    override fun getZoneId(): ZoneId {
        return ZoneId.systemDefault()
    }

    override fun getNowInMillis(): Long {
        return getZonedDateTimeNow().toInstant().toEpochMilli()
    }

    override fun getZonedDateTimeNow(): ZonedDateTime {
        return ZonedDateTime.now(getZoneId())
    }

    override fun convertStringToMillis(value: String): Long =
        ZonedDateTime.parse(value).toInstant().toEpochMilli()

    override fun convertDateAndTimeToMillis(date: String, time: String): Long {
        val dateArray = date.split("-").map { it.toInt() }
        val timeArray = time.split(":").map { it.toInt() }
        return ZonedDateTime.of(
            dateArray[0],
            dateArray[1],
            dateArray[2],
            timeArray[0],
            timeArray[1],
            0,
            0,
            getZoneId()
        ).toInstant().toEpochMilli()
    }

    override fun getWaitTime(startTime: Long, endTime: Long): Long {
        val nowZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), getZoneId())
        val expectedZoneDateTime =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), getZoneId())
        return Duration.between(nowZonedDateTime, expectedZoneDateTime).toMinutes()
    }
}

interface DateTimeConverter {
    fun getWaitTime(startTime: Long, endTime: Long): Long
    fun convertDateAndTimeToMillis(date: String, time: String): Long
    fun convertStringToMillis(value: String): Long
    fun getZonedDateTimeNow(): ZonedDateTime
    fun getNowInMillis(): Long
    fun getZoneId(): ZoneId
}

