package com.kibou.abisoyeoke_lawal.coupinapp.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kibou.abisoyeoke_lawal.coupinapp.di.CoupinRetrofit
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.CoupinServices
import com.kibou.abisoyeoke_lawal.coupinapp.models.*
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

    private val selectedCoupinsMLD = MutableLiveData<ArrayList<RewardV2>>()
    val selectedCoupinsLD : LiveData<ArrayList<RewardV2>> get() = selectedCoupinsMLD

    private val merchantMLD = MutableLiveData<MerchantV2>()
    val merchantLD : LiveData<MerchantV2> get() = merchantMLD

    private val deliveryPriceMLD = MutableLiveData<Int>()
    val deliveryPriceLD : LiveData<Int> get() = deliveryPriceMLD

    val selectedDeliveryMethod = MutableLiveData<Int>()

    val isDeliverableMLD = MutableLiveData(false)
    val addressIdMLD = MutableLiveData("")
    val expiryDateMLD = MutableLiveData<String>()

    val coupinResponseModelMLD = MutableLiveData<GetCoupinResponseModel>()

    val tempBlackListMLD = MutableLiveData<Set<String>>()


    fun setSelectedCoupins(coupins : Array<RewardV2>){
        selectedCoupinsMLD.value = coupins.toCollection(ArrayList())
    }

    fun setMerchant(merchant : MerchantV2){
        merchantMLD.value = merchant
    }

    fun setDeliveryPrice(price : Int?){
        deliveryPriceMLD.value = price
    }

    fun removeCoupin(reward : RewardV2): ArrayList<RewardV2>? {
        selectedCoupinsMLD.value?.remove(reward)
        return selectedCoupinsLD.value
    }

    fun getCoupin(getCoupinRequestModel: GetCoupinRequestModel, token : String): LiveData<Resource<GetCoupinResponseModel>> {
        val coupinService = coupinRetrofit.create(CoupinServices::class.java).getCoupin(getCoupinRequestModel, token)
        return NetworkCall<GetCoupinResponseModel>().makeCall(coupinService)
    }
}