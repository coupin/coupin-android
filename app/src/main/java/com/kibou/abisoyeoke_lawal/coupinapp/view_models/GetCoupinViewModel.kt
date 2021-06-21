package com.kibou.abisoyeoke_lawal.coupinapp.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward

class GetCoupinViewModel(application: Application) : AndroidViewModel(application) {
    private val selectedCoupinsMLD = MutableLiveData<ArrayList<Reward>>()
    val selectedCoupinsLD : LiveData<ArrayList<Reward>> get() = selectedCoupinsMLD

    val selectedDeliveryMethod = MutableLiveData<Int>()

    fun setSelectedCoupins(coupins : Array<Reward>){
        selectedCoupinsMLD.value = coupins.toCollection(ArrayList())
    }

    fun removeCoupin(reward : Reward): ArrayList<Reward>? {
        selectedCoupinsMLD.value?.remove(reward)
        return selectedCoupinsLD.value
    }
}