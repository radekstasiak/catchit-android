package io.radev.catchit.domain

import io.radev.catchit.DateTimeConverter
import io.radev.catchit.data.DataRepository
import io.radev.catchit.viewmodel.DepartureDetailsUiModel

/*
 * Created by radoslaw on 30/06/2020.
 * radev.io 2020.
 */

data class DepartureDomainModel(
    val atcocode: String,
    val name: String,
    val bearing: String,
    val indicator: String,
    val locality: String,
    val departures: List<DepartureDetailsDomainModel>
)

//todo consider adding waittime to this model as both UI models need it
data class DepartureDetailsDomainModel(
    val mode: String,
    val line: String,
    val lineName: String,
    val direction: String,
    val operator: String,
    //rename to bestDepartureEstimate timestamp
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

suspend fun DepartureDomainModel.toFavouriteDepartureAlert(
    dataRepository: DataRepository,
    dateTimeConverter: DateTimeConverter
): List<FavouriteDeparturesAlertDomainModel> =
    //        .takeIf { favLineList.contains(it.lineName }
    this.departures.sortedBy { it.bestDepartureEstimate }
        //TODO when want to display more then just next departure of favourite line - remove distinctBy
        //TODO update to display an info for the favourite lines which are not embedded in the DepartureDomainModel object
        .distinctBy { Pair(it.lineName, it.direction) }
        .mapNotNull {
            if (dataRepository.getFavouriteLineByAtcocodeAndLineName(
                    atcocode = this.atcocode,
                    lineName = it.lineName
                ).isNotEmpty()
            ) {
                FavouriteDeparturesAlertDomainModel(
                    atcocode = this.atcocode,
                    lineName = it.lineName,
                    waitTime = dateTimeConverter.getWaitTime(
                        dateTimeConverter.getNowInMillis(),
                        it.bestDepartureEstimate
                    ),
                    nextDeparture = dateTimeConverter.convertMillisToHumanFormat(it.bestDepartureEstimate),
                    direction = it.direction,
                    timestamp = it.bestDepartureEstimate,
                    stopName = this.name
                )
            } else {
                null
            }
        }









