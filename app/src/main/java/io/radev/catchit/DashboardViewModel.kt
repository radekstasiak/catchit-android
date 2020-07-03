package io.radev.catchit

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.radev.catchit.data.DataRepository
import io.radev.catchit.db.FavouriteLine
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.domain.DeparturesState
import io.radev.catchit.domain.GetDeparturesUseCase
import io.radev.catchit.domain.toDepartureDetailsUiModel
import io.radev.catchit.network.*
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
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //todo update to private
    val _postCodeMember = MutableLiveData<PostCodeMember>()
    val postCodeMember = Transformations.map(_postCodeMember) { postCodeMember ->
        postCodeMember
    }

    private val _favouriteStopList = dataRepository.getAllFavouriteStops()

    val favouriteStopList = Transformations.map(_favouriteStopList) { favouriteStopList ->
        favouriteStopList
    }
    private val _placeMemberList = MutableLiveData<List<PlaceMember>>()
    val placeMemberModelList = MediatorLiveData<DepartureMapModel>()

    private val _favouriteLineList = dataRepository.getAllFavouriteLines()
    private val _departureDetails = MutableLiveData<List<DepartureDetailsUiModel>>()
    val departureDetailsModelList = MediatorLiveData<List<DepartureDetailsUiModel>>()

    val atcocode = MutableLiveData<String>()
    private val _stopHeaderText = MutableLiveData<String>()

    init {


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
            if (_postCodeMember.value != null) {
                LatLng(
                    _postCodeMember.value!!.latitude,
                    _postCodeMember.value!!.longitude
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
            //to do this needs to be handled better
            val member = PostCodeMember(
                type = "type",
                name = "name",
                latitude = latitude,
                longitude = longitude,
                accuracy = 100
            )
            _postCodeMember.value = member
        }
        viewModelScope.launch {
            when (val result = dataRepository.getNearbyPlaces(
                longitude = longitude ?: _postCodeMember.value!!.longitude,
                latitude = latitude ?: _postCodeMember.value!!.latitude
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

    //todo
    //from this one get latLang and pass it to post code member
    // update postcode member data type
    fun getPostCodeDetails(postCode: String) {
        viewModelScope.launch {
            when (val result = dataRepository.getPostCodeDetails(postCode = postCode)) {
                is NetworkResponse.Success -> _postCodeMember.value = result.body.memberList[0]
                is NetworkResponse.ApiError -> TODO()
                is NetworkResponse.NetworkError -> TODO()
                is NetworkResponse.UnknownError -> TODO()
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