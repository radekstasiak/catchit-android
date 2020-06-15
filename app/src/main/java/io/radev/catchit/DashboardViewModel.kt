package io.radev.catchit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.radev.catchit.network.PostCodeMember

/*
 * Created by radek on 01/06/2020.
 * radev.io 2020.
 */

class DashboardViewModel : ViewModel(){
    val postCodeMember = MutableLiveData<PostCodeMember>()

    fun selectPostCodeMember(item: PostCodeMember){
        this.postCodeMember.value = item
    }
}

data class PlaceMemberModel(val name:String,
val description:String,
val atcocode:String,
val distance: String,
val isFavourite:Boolean)