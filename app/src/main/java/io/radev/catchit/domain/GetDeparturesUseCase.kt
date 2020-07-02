package io.radev.catchit.domain

import io.radev.catchit.DateTimeConverter
import io.radev.catchit.data.DataRepository
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.network.toDomainModel
import javax.inject.Inject

/*
 * Created by radoslaw on 30/06/2020.
 * radev.io 2020.
 */

interface GetDeparturesUseCase {

    suspend fun getDepartureState(atcocode: String): DeparturesState
}

class GetDeparturesInteractor @Inject constructor(
    private val dataRepository: DataRepository,
    private val dateTimeConverter: DateTimeConverter
) :
    GetDeparturesUseCase {
    override suspend fun getDepartureState(atcocode: String): DeparturesState =
        when (val result = dataRepository.getLiveTimetable(atcocode = atcocode)) {
            is NetworkResponse.Success -> DeparturesState.Success(
                data = result.body.toDomainModel(
                    dateTimeConverter = dateTimeConverter
                )
            )
            is NetworkResponse.ApiError -> DeparturesState.ApiError(code = result.code)
            is NetworkResponse.NetworkError -> DeparturesState.NetworkError
            is NetworkResponse.UnknownError -> DeparturesState.UnknownError(
                error = result.error ?: Throwable(message = "unknown")
            )
        }
}

sealed class DeparturesState {
    data class Success(val data: DepartureDomainModel) : DeparturesState()
    data class ApiError(val code: Int) : DeparturesState()
    object NetworkError : DeparturesState()
    data class UnknownError(val error: Throwable) : DeparturesState()
}




