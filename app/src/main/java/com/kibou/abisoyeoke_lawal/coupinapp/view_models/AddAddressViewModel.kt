package com.kibou.abisoyeoke_lawal.coupinapp.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.kibou.abisoyeoke_lawal.coupinapp.BuildConfig
import com.kibou.abisoyeoke_lawal.coupinapp.database.AddressDAO
import com.kibou.abisoyeoke_lawal.coupinapp.di.CoupinRetrofit
import com.kibou.abisoyeoke_lawal.coupinapp.di.GoogleMapsRetrofit
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.CoupinServices
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.PlaceSearchServices
import com.kibou.abisoyeoke_lawal.coupinapp.models.*
import com.kibou.abisoyeoke_lawal.coupinapp.utils.NetworkCall
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(application: Application, @GoogleMapsRetrofit private val mapsRetrofit : Retrofit,
@CoupinRetrofit private val coupinRetrofit: Retrofit, private val addressDAO : AddressDAO) :  AndroidViewModel(application){

    private val userLatLngMLD : MutableLiveData<LatLng?> = MutableLiveData()
    val userLatLngLD : LiveData<LatLng?> get() = userLatLngMLD

    val mldAddressSetTextFrom : MutableLiveData<AddressSetTextFrom> = MutableLiveData(AddressSetTextFrom.DEFAULT)

    fun searchPlaces(searchString : String): LiveData<Resource<PlacesSearchResponseModel>> {
        val placesSearchService = mapsRetrofit.create(PlaceSearchServices::class.java)
        return NetworkCall<PlacesSearchResponseModel>()
                .makeCall(placesSearchService.searchPlace(searchString, BuildConfig.GOOGLE_PLACES_API))
    }

    fun setUserLatLng(latLng: LatLng?) = latLng.also { userLatLngMLD.value = it }

    fun addAddressToNetwork(addressModel : AddressModel): LiveData<Resource<AddAddressResponseModel>> {
        val addressService = coupinRetrofit.create(CoupinServices::class.java)
        return NetworkCall<AddAddressResponseModel>().makeCall(addressService.addAddress(addressModel.token,
                addressModel.longitude, addressModel.latitude, addressModel.mobileNumber, addressModel.address))
    }

    fun addAddressToDB(address : AddressResponseModel){
        viewModelScope.launch {
            addressDAO.insertAddress(address)
        }
    }

}