package com.kibou.abisoyeoke_lawal.coupinapp.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kibou.abisoyeoke_lawal.coupinapp.di.CoupinRetrofit
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.CoupinServices
import com.kibou.abisoyeoke_lawal.coupinapp.models.GetCoupinRequestModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.GetCoupinResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward
import com.kibou.abisoyeoke_lawal.coupinapp.utils.NetworkCall
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Retrofit
import java.nio.channels.NetworkChannel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@HiltViewModel
class GetCoupinViewModel @Inject constructor(application: Application, @CoupinRetrofit private val coupinRetrofit: Retrofit) : AndroidViewModel(application) {

    private val selectedCoupinsMLD = MutableLiveData<ArrayList<Reward>>()
    val selectedCoupinsLD : LiveData<ArrayList<Reward>> get() = selectedCoupinsMLD

    private val merchantMLD = MutableLiveData<Merchant>()
    val merchantLD : LiveData<Merchant> get() = merchantMLD

    private val deliveryPriceMLD = MutableLiveData(0)
    val deliveryPriceLD : LiveData<Int> get() = deliveryPriceMLD

    val selectedDeliveryMethod = MutableLiveData<Int>()

    val isDeliverableMLD = MutableLiveData(false)
    val addressIdMLD = MutableLiveData("")
    val expiryDateMLD = MutableLiveData<String>()

    val coupinResponseModelMLD = MutableLiveData<GetCoupinResponseModel>()

    val rewardQuantityMLD = MutableLiveData<HashMap<String, Int>>()

    fun setSelectedCoupins(coupins : Array<Reward>){
        selectedCoupinsMLD.value = coupins.toCollection(ArrayList())
    }

    fun setMerchant(merchant : Merchant){
        merchantMLD.value = merchant
    }

    fun setDeliveryPrice(price : Int){
        deliveryPriceMLD.value = price
    }

    fun removeCoupin(reward : Reward): ArrayList<Reward>? {
        selectedCoupinsMLD.value?.remove(reward)
        return selectedCoupinsLD.value
    }

    fun getCoupin(getCoupinRequestModel: GetCoupinRequestModel, token : String): LiveData<Resource<GetCoupinResponseModel>> {
        val coupinService = coupinRetrofit.create(CoupinServices::class.java).getCoupin(getCoupinRequestModel, token)
        return NetworkCall<GetCoupinResponseModel>().makeCall(coupinService)
    }
}