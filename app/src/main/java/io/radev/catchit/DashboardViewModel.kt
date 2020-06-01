package io.radev.catchit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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