package io.radev.catchit.domain

import io.radev.catchit.DateTimeConverter
import io.radev.catchit.data.DataRepository
import io.radev.catchit.db.toUniqueAtcocodeList
import io.radev.catchit.network.DepartureResponse
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.network.NetworkResponse.Success
import io.radev.catchit.network.toDomainModel
import io.radev.catchit.viewmodel.FavouriteDepartureAlert
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/*
 * Created by radoslaw on 27/06/2020.
 * radev.io 2020.
 */

interface UpdateFavouriteDeparturesAlertUseCase {

    suspend fun getNextDepartures(atcocode: List<String>): List<String>
    suspend fun getFavouriteDeparturesUpdate(): List<FavouriteDepartureUpdateState>
//    suspend fun filterFavouriteLines(domainModel: DepartureDomainModel): List<FavouriteDepartureAlert>
}

class UpdateFavouriteDeparturesAlertInteractor @Inject constructor(
    private val dataRepository: DataRepository,
    private val dateTimeConverter: DateTimeConverter
) :
    UpdateFavouriteDeparturesAlertUseCase {
    override suspend fun getNextDepartures(atcocodeList: List<String>): List<String> =
        coroutineScope {
            mapToDepartureResultList(atcocodeList.map { atcocode ->
                async {
                    dataRepository.getLiveTimetable(atcocode)
                }
            }
                .map { it.await() }
                .filterIsInstance<Success<DepartureResponse>>()
            )

        }

    override suspend fun getFavouriteDeparturesUpdate(): List<FavouriteDepartureUpdateState> {
        return coroutineScope {
            val atcocodeList = dataRepository.getAllFavouriteLinesSync().toUniqueAtcocodeList()
            atcocodeList.map { atcocode ->
                async { dataRepository.getLiveTimetable(atcocode) }
            }.map {
                when (val result = it.await()) {
                    is Success -> {
                        val domainModel =  result.body.toDomainModel(dateTimeConverter = dateTimeConverter)
                        val departureAlertList =domainModel.toFavouriteDepartureAlert(dataRepository=dataRepository,dateTimeConverter = dateTimeConverter)
                        FavouriteDepartureUpdateState.Success(list = departureAlertList)
                    }
                    is NetworkResponse.ApiError -> FavouriteDepartureUpdateState.ApiError(code = result.code)
                    is NetworkResponse.NetworkError -> FavouriteDepartureUpdateState.NetworkError
                    is NetworkResponse.UnknownError -> FavouriteDepartureUpdateState.UnknownError(
                        error = result.error ?: Throwable("unknown")
                    )
                }
            }
        }

    }


    private fun mapToDepartureResultList(list: List<NetworkResponse.Success<DepartureResponse>>): List<String> =
        list.map { it.body.atcocode ?: "" }

}

data class FavouriteDeparturesAlertDomainModel(
    val atcocode: String,
    val lineName: String,
    val waitTime: String,
    val nextDeparture: String,
    val direction: String,
    val timestamp: Long,
    val stopName: String
)

fun FavouriteDeparturesAlertDomainModel.toUiModel(): FavouriteDepartureAlert =
    FavouriteDepartureAlert(
        atcocode = this.atcocode,
        lineName = this.lineName,
        waitTime = this.waitTime,
        nextDeparture = this.nextDeparture,
        direction = this.direction,
        timestamp = this.timestamp,
        stopName = this.stopName
    )

sealed class FavouriteDepartureUpdateState() {
    data class Success(val list: List<FavouriteDeparturesAlertDomainModel>) : FavouriteDepartureUpdateState()
    data class ApiError(val code: Int) : FavouriteDepartureUpdateState()
    object NetworkError : FavouriteDepartureUpdateState()
    data class UnknownError(val error: Throwable) : FavouriteDepartureUpdateState()
}

