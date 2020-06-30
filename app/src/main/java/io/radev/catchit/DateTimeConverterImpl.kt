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

    override fun convertMillisToHumanFormat(timestamp: Long): String {
        val date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), getZoneId())

        return String.format("%s:%s", date.hour, date.minute)

    }

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

    override fun getWaitTime(startTime: Long, endTime: Long): String {
        val nowZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), getZoneId())
        val expectedZoneDateTime =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), getZoneId())
        val diff = Duration.between(nowZonedDateTime, expectedZoneDateTime).toMinutes()
        return if (diff > 0) "${diff}m" else "DUE"
    }
}

interface DateTimeConverter {
    fun getWaitTime(startTime: Long, endTime: Long): String
    fun convertDateAndTimeToMillis(date: String, time: String): Long
    fun convertStringToMillis(value: String): Long
    fun convertMillisToHumanFormat(value: Long): String
    fun getZonedDateTimeNow(): ZonedDateTime
    fun getNowInMillis(): Long
    fun getZoneId(): ZoneId
}

