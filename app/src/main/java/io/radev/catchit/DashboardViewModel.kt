package io.radev.catchit

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.radev.catchit.network.ApiService
import io.radev.catchit.network.DepartureDetails
import io.radev.catchit.network.PlaceMember
import io.radev.catchit.network.PostCodeMember
import kotlinx.android.synthetic.main.fragment_second.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/*
 * Created by radek on 01/06/2020.
 * radev.io 2020.
 */

class DashboardViewModel @ViewModelInject constructor(
    private val apiService: ApiService,
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

    fun selectAtcocode(value: String){
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
                stopHeaderText.value =  "${response.body()!!.name} - ${response.body()!!.atcocode}"
                if (response.body() != null && response.body()!!.departures != null) departureDetails.value = response.body()!!.departures!!.getValue("all")
            }
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