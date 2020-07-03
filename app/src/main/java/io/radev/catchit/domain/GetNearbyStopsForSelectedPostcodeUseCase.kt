package io.radev.catchit.domain

import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.network.PlaceMember
import javax.inject.Inject

/*
 * Created by radoslaw on 03/07/2020.
 * radev.io 2020.
 */

interface GetNearbyStopsForSelectedPostcodeUseCase {
    suspend fun getNearbyStops(postCode: String): PlaceMembersState
}

class GetNearbyStopsForSelectedPostcodeInteractor @Inject constructor(val dataRepository: DataRepository) :
    GetNearbyStopsForSelectedPostcodeUseCase {
    override suspend fun getNearbyStops(postcode: String): PlaceMembersState {
        return when (val postCodeResult = dataRepository.getPostCodeDetails(postCode = postcode)) {
            is NetworkResponse.Success -> if (postCodeResult.body.memberList.isNotEmpty()) {
                val longitude = postCodeResult.body.memberList[0].longitude
                val latitude = postCodeResult.body.memberList[0].latitude
                when (val nearbyPlacesResult =
                    dataRepository.getNearbyPlaces(latitude = latitude, longitude = longitude)) {
                    is NetworkResponse.Success -> PlaceMembersState.Success(
                        data = nearbyPlacesResult.body.memberList,
                        longitude = longitude,
                        latitude = latitude
                    )
                    is NetworkResponse.ApiError -> PlaceMembersState.ApiError(code = nearbyPlacesResult.code)
                    is NetworkResponse.NetworkError -> PlaceMembersState.NetworkError
                    is NetworkResponse.UnknownError -> PlaceMembersState.UnknownError(
                        error = nearbyPlacesResult.error ?: Throwable("unknown")
                    )
                }

            } else {
                PlaceMembersState.PostCodeNotFound
            }
            is NetworkResponse.ApiError -> PlaceMembersState.ApiError(code = postCodeResult.code)
            is NetworkResponse.NetworkError -> PlaceMembersState.NetworkError
            is NetworkResponse.UnknownError -> PlaceMembersState.UnknownError(
                error = postCodeResult.error ?: Throwable("unknown")
            )
        }
    }

}

sealed class PlaceMembersState {
    data class Success(val data: List<PlaceMember>, val longitude: Double, val latitude: Double) :
        PlaceMembersState()

    object PostCodeNotFound : PlaceMembersState()
    data class ApiError(val code: Int) : PlaceMembersState()
    object NetworkError : PlaceMembersState()
    data class UnknownError(val error: Throwable) : PlaceMembersState()
}