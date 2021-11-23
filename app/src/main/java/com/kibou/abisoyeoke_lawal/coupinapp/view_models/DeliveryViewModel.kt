package com.kibou.abisoyeoke_lawal.coupinapp.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kibou.abisoyeoke_lawal.coupinapp.database.AddressDAO
import com.kibou.abisoyeoke_lawal.coupinapp.di.CoupinRetrofit
import com.kibou.abisoyeoke_lawal.coupinapp.di.GokadaRetrofit
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.CoupinServices
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.GokadaPriceEstimateService
import com.kibou.abisoyeoke_lawal.coupinapp.models.*
import com.kibou.abisoyeoke_lawal.coupinapp.utils.NetworkCall
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(application: Application,@CoupinRetrofit private val coupinRetrofit: Retrofit,
                                            private val addressDAO : AddressDAO, @GokadaRetrofit private val gokadaRetrofit :
                                            Retrofit) : AndroidViewModel(application) {

    fun getAddressesFromNetwork(token : String): LiveData<Resource<GetAddressesResponseModel>> {
        val addressService = coupinRetrofit.create(CoupinServices::class.java)
        return NetworkCall<GetAddressesResponseModel>().makeCall(addressService.getAddress(token))
    }

    fun addAddressesToDB(addresses : List<AddressResponseModel>){
        viewModelScope.launch {
            addressDAO.insertAddresses(addresses)
        }
    }

    fun getAddressesFromDB() : LiveData<List<AddressResponseModel>> {
        return addressDAO.getAddresses()
    }

    fun getDeliveryEstimate(gokadaOrderEstimateRequestBody : GokadaOrderEstimateRequestBody): LiveData<Resource<GokadaOrderEstimateResponse>> {
        val gokadaDeliveryService = gokadaRetrofit.create(GokadaPriceEstimateService::class.java)
        return NetworkCall<GokadaOrderEstimateResponse>().makeCall(gokadaDeliveryService.getPriceEstimate(gokadaOrderEstimateRequestBody))
    }

    fun getKwikDeliveryEstimate(merchantId : String, addressId: String, totalCost: Float):
            LiveData<Resource<KwikOrderEstimateResponse>> {
        val kwikServices = coupinRetrofit.create(CoupinServices::class.java)
        return NetworkCall<KwikOrderEstimateResponse>().makeCall(kwikServices.getKwikPriceEstimate(merchantId, addressId, totalCost))
    }
}
