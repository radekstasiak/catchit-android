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
    suspend fun filterFavouriteLines(domainModel: DepartureDomainModel): List<FavouriteDepartureAlert>
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
                //todo validate if requests are done in parallel if not separate into two maps
                when (val result = it.await()) {
                    is Success -> {
                        FavouriteDepartureUpdateState.Success(
                            result.body.toDomainModel(
                                dateTimeConverter = dateTimeConverter
                            )
                        )
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

    //todo update method above to return this
    override suspend fun filterFavouriteLines(domainModel: DepartureDomainModel): List<FavouriteDepartureAlert> {
        val favouriteLines = dataRepository.getFavouriteLinesByAtcocode(atcocode = domainModel.atcocode)
        val list = favouriteLines.map { it.lineName }
        return domainModel.toFavouriteDepartureAlert(
            favLineList = list,
            dateTimeConverter = dateTimeConverter
        )
    }

    private fun mapToDepartureResultList(list: List<NetworkResponse.Success<DepartureResponse>>): List<String> =
        list.map { it.body.atcocode ?: "" }

}

sealed class FavouriteDepartureUpdateState() {
    data class Success(val data: DepartureDomainModel) : FavouriteDepartureUpdateState()
    data class ApiError(val code: Int) : FavouriteDepartureUpdateState()
    object NetworkError : FavouriteDepartureUpdateState()
    data class UnknownError(val error: Throwable) : FavouriteDepartureUpdateState()
}

