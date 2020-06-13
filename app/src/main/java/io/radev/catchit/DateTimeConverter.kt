package io.radev.catchit

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

/*
 * Created by radoslaw on 13/06/2020.
 * radev.io 2020.
 */

class DateTimeConverter {

    fun getZoneId(): ZoneId {
        return ZoneId.systemDefault()
    }

    fun getNowInMillis(): Long {
        return getZonedDateTimeNow().toInstant().toEpochMilli()
    }

    private fun getZonedDateTimeNow(): ZonedDateTime {
        return ZonedDateTime.now(getZoneId())
    }

    fun convertStringToMillis(value: String): Long =
        ZonedDateTime.parse(value).toInstant().toEpochMilli()

    fun convertDateAndTimeToMillis(date: String, time: String): Long {
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

    fun getWaitingTime(startTime: Long, endTime: Long): Long {
        val nowZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), getZoneId())
        val expectedZoneDateTime =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), getZoneId())
        return Duration.between(nowZonedDateTime, expectedZoneDateTime).toMinutes()
    }
}