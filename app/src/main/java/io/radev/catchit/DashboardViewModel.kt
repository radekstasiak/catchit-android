package io.radev.catchit

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import io.radev.catchit.data.DataRepository
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.network.ApiService
import io.radev.catchit.network.DepartureDetails
import io.radev.catchit.network.PostCodeMember
import io.radev.catchit.network.toPlaceMemberModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * Created by radek on 01/06/2020.
 * radev.io 2020.
 */

class DashboardViewModel @ViewModelInject constructor(
    private val apiService: ApiService,
    private val dataRepository: DataRepository,
    private val converter: DateTimeConverter,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _postCodeMember = MutableLiveData<PostCodeMember>()
    val postCodeMember = Transformations.map(_postCodeMember) { postCodeMember ->
        postCodeMember
    }


    private val _placeMemberList = MutableLiveData<List<PlaceMemberModel>>()
    val placeMemberList = Transformations.map((_placeMemberList)) { placeMemberList ->
        placeMemberList
    }

    private val _departureDetails = MutableLiveData<List<DepartureDetails>>()
    val departureDetails = Transformations.map((_departureDetails)) { departureDetails ->
        departureDetails
    }

    val atcocode = MutableLiveData<String>()
    private val _stopHeaderText = MutableLiveData<String>()
    val stopHeaderText = Transformations.map(_stopHeaderText) { headerText ->
        headerText
    }

    fun selectAtcocode(value: String) {
        this.atcocode.value = value
    }

    fun getNearbyPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getNearbyPlaces(
                lon = postCodeMember.value!!.longitude,
                lat = postCodeMember.value!!.latitude
            )
            val result = arrayListOf<PlaceMemberModel>()
            for (member in response.memberList) {
                val isFavourite = dataRepository.findFavouriteLineByAtcocode(
                    atcocode = member.atcocode
                ).isNotEmpty()
                result.add(member.toPlaceMemberModel(favourite = isFavourite))
            }
            withContext(Dispatchers.Main) { _placeMemberList.value = result }
        }
    }

    fun getLiveTimetable() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getLiveTimetable(atcocode.value!!)
            withContext(Dispatchers.Main) {
                if (response.departures != null) _departureDetails.value =
                    response.departures.getValue("all")
                _stopHeaderText.value = "${response.name} - ${response.atcocode}"
            }

        }
    }

    fun getPostCodeDetails(postCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getPostCodeDetails(query = postCode)
            withContext(Dispatchers.Main) {
                _postCodeMember.value = response.memberList[0]
            }
        }
    }


    //continue with
//https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-6-working-with-architecture-components/lesson-14-room,-livedata,-viewmodel/14-1-a-room-livedata-viewmodel/14-1-a-room-livedata-viewmodel.html
//https://medium.com/androiddevelopers/viewmodels-persistence-onsaveinstancestate-restoring-ui-state-and-loaders-fc7cc4a6c090
//https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
//https://medium.com/androiddevelopers/livedata-beyond-the-viewmodel-reactive-patterns-using-transformations-and-mediatorlivedata-fda520ba00b7
    fun updateFavouriteStop(atcocode: String, favourite: Boolean) {
        if (favourite) {
            val entity = FavouriteStop(
                createdAt = converter.getNowInMillis(),
                modifiedAt = converter.getNowInMillis(),
                atcocode = atcocode
            )
            viewModelScope.launch { dataRepository.addFavouriteStop(favouriteStop = entity) }

        } else {
            viewModelScope.launch { dataRepository.removeFavouriteStopByAtcocode(atcocode = atcocode) }
        }


    }

}

data class PlaceMemberModel(
    val name: String,
    val description: String,
    val atcocode: String,
    val distance: String,
    val isFavourite: Boolean
)