package io.radev.catchit.viewmodel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.radev.catchit.DateTimeConverter
import io.radev.catchit.data.DataRepository
import io.radev.catchit.db.FavouriteLine
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.domain.*
import io.radev.catchit.network.NetworkResponse
import io.radev.catchit.network.PlaceMember
import io.radev.catchit.network.toDeparturesMap
import io.radev.catchit.network.toPlaceMemberModel
import kotlinx.coroutines.launch

/*
 * Created by radek on 01/06/2020.
 * radev.io 2020.
 */
//how to test
//https://medium.com/@harmittaa/retrofit-2-6-0-with-koin-and-coroutines-testing-your-layers-42d2a71566f1
class DashboardViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository,
    private val converter: DateTimeConverter,
    private val getDeparturesUseCase: GetDeparturesUseCase,
    private val updateFavouriteDeparturesAlertUseCase: UpdateFavouriteDeparturesAlertUseCase,
    private val getNearbyStopsForSelectedPostcodeUseCase: GetNearbyStopsForSelectedPostcodeUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val TAG = "DashboardViewModel"

    //todo update with initial values
    val _userLatLang = MutableLiveData<LatitudeLongitude>()

    private val _favouriteDeparturesAlertList = MutableLiveData<List<FavouriteDepartureAlert>>()
    val favouriteDeparturesAlertList = MediatorLiveData<List<FavouriteDepartureAlert>>()
//        Transformations.map(_favouriteDeparturesAlertList) { favouriteDeparturesAlertList ->
//            favouriteDeparturesAlertList
//        }

    private val _favouriteStopList = dataRepository.getAllFavouriteStops()

    private val _placeMemberList = MutableLiveData<List<PlaceMember>>()
    val placeMemberModelList = MediatorLiveData<DepartureMapModel>()

    private val _favouriteLineList = dataRepository.getAllFavouriteLines()
    private val _departureDetails = MutableLiveData<List<DepartureDetailsUiModel>>()
    val departureDetailsModelList = MediatorLiveData<List<DepartureDetailsUiModel>>()

    val atcocode = MutableLiveData<String>()
    private val _stopHeaderText = MutableLiveData<String>()

    init {

        favouriteDeparturesAlertList.addSource(_favouriteLineList) {
            if (favouriteDeparturesAlertList.value != null && _favouriteLineList.value != null) {
                val result = arrayListOf<FavouriteDepartureAlert>()

                for (alert in favouriteDeparturesAlertList.value!!) {
                    if (_favouriteLineList.value!!.find { it.atcocode == alert.atcocode && it.lineName == alert.lineName } != null) result.add(
                        alert
                    )
                }
                favouriteDeparturesAlertList.value = result
            }
        }

        favouriteDeparturesAlertList.addSource(_favouriteDeparturesAlertList) {
            favouriteDeparturesAlertList.value = _favouriteDeparturesAlertList.value
        }
        placeMemberModelList.addSource(_favouriteStopList) { _ ->
            placeMemberModelList.value =
                combineFavouriteStopData(_favouriteStopList, _placeMemberList)
        }
        placeMemberModelList.addSource(_placeMemberList) {
            placeMemberModelList.value =
                combineFavouriteStopData(_favouriteStopList, _placeMemberList)
        }


        departureDetailsModelList.addSource(_favouriteLineList) { _ ->
            if (atcocode.value != null) {
                departureDetailsModelList.value = combineFavouriteLineData(
                    atcocode = atcocode.value!!,
                    apiDepartureDetailsResult = _departureDetails,
                    dbFavouriteLineResult = _favouriteLineList
                )
            }
        }

        departureDetailsModelList.addSource(_departureDetails) { _ ->
            if (atcocode.value != null) {
                departureDetailsModelList.value = combineFavouriteLineData(
                    atcocode = atcocode.value!!,
                    apiDepartureDetailsResult = _departureDetails,
                    dbFavouriteLineResult = _favouriteLineList
                )
            }
        }


    }

    val stopHeaderText = Transformations.map(_stopHeaderText) { headerText ->
        headerText
    }

    private fun combineFavouriteLineData(
        atcocode: String,
        dbFavouriteLineResult: LiveData<List<FavouriteLine>>,
        apiDepartureDetailsResult: LiveData<List<DepartureDetailsUiModel>>
    ): List<DepartureDetailsUiModel> {
        val result = arrayListOf<DepartureDetailsUiModel>()
        if (apiDepartureDetailsResult.value != null) {
            val dbFavouriteLineList =
                if (dbFavouriteLineResult.value != null) dbFavouriteLineResult.value else arrayListOf()
            for (item in apiDepartureDetailsResult.value!!) {
                val isFavourite =
                    dbFavouriteLineList!!.find { it.atcocode == atcocode && it.lineName == item.lineName }
                result.add(
                    item.copy(
                        isFavourite = isFavourite != null
                    )
                )
            }
        }


        return result
    }

    private fun combineFavouriteStopData(
        dbFavouriteStopResult: LiveData<List<FavouriteStop>>,
        apiPlaceMemberResult: LiveData<List<PlaceMember>>
    ): DepartureMapModel {
        val result = arrayListOf<PlaceMemberModel>()
        if (apiPlaceMemberResult.value != null) {
            val dbFavouriteStopList =
                if (dbFavouriteStopResult.value != null) dbFavouriteStopResult.value else arrayListOf()
            for (member in apiPlaceMemberResult.value!!) {
                val isFavourite = dbFavouriteStopList!!.find { it.atcocode == member.atcocode }
                result.add(member.toPlaceMemberModel(favourite = isFavourite != null))
            }
        }

        return result.toDeparturesMap(
            if (_userLatLang.value != null) {
                LatLng(
                    _userLatLang.value!!.latitude,
                    _userLatLang.value!!.longitude
                )
            } else {
                LatLng(0.0, 0.0)
            }
        )
    }

    fun selectAtcocode(value: String) {
        this.atcocode.value = value
    }

    fun getNearbyPlaces(longitude: Double? = null, latitude: Double? = null) {
        if (longitude != null && latitude != null) {
            _userLatLang.value = LatitudeLongitude(
                latitude = latitude,
                longitude = longitude
            )
        }
        viewModelScope.launch {
            when (val result = dataRepository.getNearbyPlaces(
                longitude = longitude ?: _userLatLang.value!!.longitude,
                latitude = latitude ?: _userLatLang.value!!.latitude
            )) {
                is NetworkResponse.Success -> _placeMemberList.value = result.body.memberList
                is NetworkResponse.ApiError -> TODO()
                is NetworkResponse.NetworkError -> TODO()
                is NetworkResponse.UnknownError -> TODO()
            }
        }

    }


    fun getLiveTimetable() {
        viewModelScope.launch {
            when (val result =
                getDeparturesUseCase.getDepartureState(atcocode = atcocode.value!!)) {
                is DeparturesState.Success -> {
                    _departureDetails.value = result.data.departures.toDepartureDetailsUiModel(
                        atcocode = result.data.atcocode,
                        dateTimeConverter = converter
                    )
                    _stopHeaderText.value = "${result.data.name} - ${result.data.atcocode}"
                }
                is DeparturesState.ApiError -> {
                    //todo maybe keep live data as State type
                }
                DeparturesState.NetworkError -> {
                }
                is DeparturesState.UnknownError -> {
                }
            }
        }

    }


    fun getNearbyStopsWithPostcode(postCode: String) {
        viewModelScope.launch {
            when (val result =
                getNearbyStopsForSelectedPostcodeUseCase.getNearbyStops(postCode = postCode)) {
                is PlaceMembersState.Success -> {
                    _userLatLang.value =
                        LatitudeLongitude(
                            latitude = result.latitude,
                            longitude = result.longitude
                        )
                    _placeMemberList.value = result.data
                }
                PlaceMembersState.PostCodeNotFound -> {
                }
                is PlaceMembersState.ApiError -> {
                }
                PlaceMembersState.NetworkError -> {
                }
                is PlaceMembersState.UnknownError -> {
                }
            }
        }
    }


    //continue with
//https://medium.com/androiddevelopers/viewmodels-persistence-onsaveinstancestate-restoring-ui-state-and-loaders-fc7cc4a6c090
//https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
    fun updateFavouriteStop(atcocode: String, favourite: Boolean) {
        viewModelScope.launch {
            if (favourite) {
                val entity = FavouriteStop(
                    createdAt = converter.getNowInMillis(),
                    modifiedAt = converter.getNowInMillis(),
                    atcocode = atcocode
                )
                dataRepository.addFavouriteStop(favouriteStop = entity)
            } else {
                dataRepository.removeFavouriteStopByAtcocode(atcocode = atcocode)
            }
        }
    }

    fun updateFavouriteLine(atcocode: String, lineName: String, favourite: Boolean) {
        viewModelScope.launch {
            if (favourite) {
                val entity = FavouriteLine(
                    createdAt = converter.getNowInMillis(),
                    modifiedAt = converter.getNowInMillis(),
                    atcocode = atcocode,
                    lineName = lineName
                )

                dataRepository.addFavouriteLine(favouriteLine = entity)
            } else {
                dataRepository.removeFavouriteLineByAtcocodeAndLineName(
                    atcocode = atcocode,
                    lineName = lineName
                )
            }
        }
    }

    //TODO test this one and scope out from view model
    fun updateFavouriteDeparturesList() {
        viewModelScope.launch {
            val result = updateFavouriteDeparturesAlertUseCase.getFavouriteDeparturesUpdate()
            val alertList = arrayListOf<FavouriteDepartureAlert>()
            for (item in result) {
                when (item) {
                    is FavouriteDepartureUpdateState.Success -> {
                        for (departureAlert in item.list) {
                            alertList.add(departureAlert.toUiModel())
                        }
//                        alertList.addAll(
//                            updateFavouriteDeparturesAlertUseCase.filterFavouriteLines(
//                                item.data
//                            )
//                        )
                    }
                    is FavouriteDepartureUpdateState.ApiError -> Log.d(
                        TAG,
                        "api error ${item.code}"
                    )
                    FavouriteDepartureUpdateState.NetworkError -> Log.d(TAG, "network error}")
                    is FavouriteDepartureUpdateState.UnknownError -> Log.d(
                        TAG,
                        "unknown error ${item.error.message}"
                    )
                }
            }
            _favouriteDeparturesAlertList.value = alertList.sortedBy { it.timestamp }

        }
    }

}

data class DepartureMapModel(
    val departuresList: List<PlaceMemberModel>,
    val userLatLng: LatLng,
    val latLngBounds: LatLngBounds
)


data class PlaceMemberModel(
    val name: String,
    val description: String,
    val atcocode: String,
    val distance: String,
    val longitude: Double,
    val latitude: Double,
    val isFavourite: Boolean
)

data class DepartureDetailsModel(
    val departureDate: String,
    val departureTime: String,
    val lineName: String,
    val direction: String,
    val operator: String,
    val mode: String,
    val atcocode: String,
    val isFavourite: Boolean,
    val waitTime: String
)

data class FavouriteDepartureAlert(
    val atcocode: String,
    val lineName: String,
    val waitTime: String,
    val nextDeparture: String,
    val direction: String,
    val timestamp: Long,
    val stopName: String
)

//TODO handle cancellations
data class DepartureDetailsUiModel(
    val timestamp: Long,
    val nextDeparture: String,
    val waitTime: String,
    val lineName: String,
    val operatorName: String,
    val direction: String,
    val atcocode: String,
    val isFavourite: Boolean
)

data class LatitudeLongitude(
    val latitude: Double,
    val longitude: Double
)