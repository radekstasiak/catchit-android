package io.radev.catchit.domain

import io.radev.catchit.DateTimeConverter
import io.radev.catchit.DepartureDetailsUiModel

/*
 * Created by radoslaw on 30/06/2020.
 * radev.io 2020.
 */

data class DepartureDomainModel(
    val atcocode: String,
    val smsCode: String,
    val requestTime: String,
    val name: String,
    val stopName: String,
    val bearing: String,
    val indicator: String,
    val locality: String,
    val location: Pair<Double, Double>,
    val departures: List<DepartureDetailsDomainModel>
)

data class DepartureDetailsDomainModel(
    val mode: String,
    val line: String,
    val lineName: String,
    val direction: String,
    val operator: String,
    val bestDepartureEstimate: Long,
    //TODO check what is possible status and handle it
    //val departureStatus: String,
    val source: String,
    val dir: String,
    val operatorName: String,
    val id: String
)

fun List<DepartureDetailsDomainModel>.toDepartureDetailsUiModel(
    atcocode: String,
    dateTimeConverter: DateTimeConverter
): List<DepartureDetailsUiModel> =
    this.sortedBy { it.bestDepartureEstimate }
        .distinctBy { Pair(it.lineName, it.direction) }
        .map {
            DepartureDetailsUiModel(
                timestamp = it.bestDepartureEstimate,
                nextDeparture = dateTimeConverter.convertMillisToHumanFormat(it.bestDepartureEstimate),
                waitTime = dateTimeConverter.getWaitTime(
                    dateTimeConverter.getNowInMillis(),
                    it.bestDepartureEstimate
                ),
                lineName = it.lineName,
                operatorName = it.operatorName,
                direction = it.direction,
                atcocode = atcocode,
                isFavourite = false
            )
        }






