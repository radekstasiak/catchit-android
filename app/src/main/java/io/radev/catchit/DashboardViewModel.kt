package io.radev.catchit

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.radev.catchit.data.DataRepository
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.network.ApiService
import io.radev.catchit.network.DepartureDetails
import io.radev.catchit.network.PlaceMember
import io.radev.catchit.network.PostCodeMember
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

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
    val postCodeMember = MutableLiveData<PostCodeMember>()
    val placeMemberList = MutableLiveData<List<PlaceMember>>()
    val departureDetails = MutableLiveData<List<DepartureDetails>>()
    val atcocode = MutableLiveData<String>()
    val stopHeaderText = MutableLiveData<String>()

    fun selectPostCodeMember(item: PostCodeMember) {
        this.postCodeMember.value = item
    }

    fun selectAtcocode(value: String) {
        this.atcocode.value = value
    }

    fun getNearbyPlaces() {
        val request = apiService.getNearbyPlaces(
            lon = postCodeMember.value!!.longitude,
            lat = postCodeMember.value!!.latitude
        )
        doAsync {
            val response = request.execute()
            uiThread {
                if (response.body() != null) placeMemberList.value = response.body()!!.memberList
            }
        }
    }

    fun getLiveTimetable() {
        val request = apiService.getLiveTimetable(atcocode.value!!)
        doAsync {
            val response = request.execute()
            uiThread {
                stopHeaderText.value = "${response.body()!!.name} - ${response.body()!!.atcocode}"
                if (response.body() != null && response.body()!!.departures != null) departureDetails.value =
                    response.body()!!.departures!!.getValue("all")
            }
        }
    }

    //continue with
    //https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-6-working-with-architecture-components/lesson-14-room,-livedata,-viewmodel/14-1-a-room-livedata-viewmodel/14-1-a-room-livedata-viewmodel.html
    //https://medium.com/androiddevelopers/viewmodels-persistence-onsaveinstancestate-restoring-ui-state-and-loaders-fc7cc4a6c090
    //https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
    fun updateFavouriteStop(atcocode: String, favourite: Boolean) {
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

data class PlaceMemberModel(
    val name: String,
    val description: String,
    val atcocode: String,
    val distance: String,
    val isFavourite: Boolean
)