package io.radev.catchit.domain

import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.DepartureResponse
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.network.NetworkResponse.Success
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/*
 * Created by radoslaw on 27/06/2020.
 * radev.io 2020.
 */

interface GetUpdatedTimetableForFavouritesUseCase {

    suspend fun getNextDepartures(atcocode: List<String>): List<String>
}

class GetUpdatedTimetableForFavouritesInteractor(private val dataRepository: DataRepository) :
    GetUpdatedTimetableForFavouritesUseCase {
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

    private fun mapToDepartureResultList(list: List<NetworkResponse.Success<DepartureResponse>>): List<String> =
        list.map { it.body.atcocode ?: "" }

}

