package com.kibou.abisoyeoke_lawal.coupinapp.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kibou.abisoyeoke_lawal.coupinapp.di.CoupinRetrofit
import com.kibou.abisoyeoke_lawal.coupinapp.di.GoogleMapsRetrofit
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.AddressService
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddAddressResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.GetAddressesResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.utils.NetworkCall
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class AddressBookViewModel @Inject constructor(application: Application, @CoupinRetrofit private val coupinRetrofit: Retrofit) : AndroidViewModel(application) {

    fun getAddresses(token : String): LiveData<Resource<GetAddressesResponseModel>> {
        val addressService = coupinRetrofit.create(AddressService::class.java)
        return NetworkCall<GetAddressesResponseModel>().makeCall(addressService.getAddress(token))
    }
}