package com.kibou.abisoyeoke_lawal.coupinapp.interfaces

import com.kibou.abisoyeoke_lawal.coupinapp.models.AddAddressResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.GetAddressesResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.PlacesSearchResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.ReverseGeocodingResponseModel
import retrofit2.Call
import retrofit2.http.*

interface PlacesSearchRecyclerClickListener{
    fun onPlacesSearchRecyclerClick(mainText: String?, secText : String?, placeId: String?)
}


interface PlaceSearchService {
    @GET("maps/api/place/autocomplete/json?")
    fun searchPlace(@Query("input") input : String, @Query("key") key : String) : Call<PlacesSearchResponseModel>

    @GET("maps/api/geocode/json?")
    fun getAddress(@Query("latlng") latlng : String, @Query("key") key : String) : Call<ReverseGeocodingResponseModel>
}

interface AddressService{
    @FormUrlEncoded
    @POST("customer/addresses")
    fun addAddress(@Header("Authorization") auth : String, @Field("longitude") longitude : Double, @Field("latitude") latitude :
    Double, @Field("mobileNumber") mobileNumber : String, @Field("address") address : String) : Call<AddAddressResponseModel>

    @GET("customer/addresses")
    fun getAddress(@Header("Authorization") auth : String) : Call<GetAddressesResponseModel>
}