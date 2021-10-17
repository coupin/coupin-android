package com.kibou.abisoyeoke_lawal.coupinapp.interfaces

import com.kibou.abisoyeoke_lawal.coupinapp.models.*
import retrofit2.Call
import retrofit2.http.*

interface PlacesSearchRecyclerClickListener{
    fun onPlacesSearchRecyclerClick(mainText: String?, secText : String?, placeId: String?)
}

interface AddressBookItemClickListener{
    fun onAddressCancelClick(addressModel : AddressResponseModel)
}

interface DeliveryAddressItemClickListener{
    fun onAddressClick(addressModel : AddressResponseModel)
}

interface ReviewSelectionCancelClickListener{
    fun onCancelClick(reward: RewardV2)
}

interface PlaceSearchServices {
    @GET("maps/api/place/autocomplete/json?")
    fun searchPlace(@Query("input") input : String, @Query("key") key : String) : Call<PlacesSearchResponseModel>

    @GET("maps/api/geocode/json?")
    fun getAddress(@Query("latlng") latlng : String, @Query("key") key : String) : Call<ReverseGeocodingResponseModel>
}

interface CoupinServices{
    @FormUrlEncoded
    @POST("customer/addresses")
    fun addAddress(@Header("Authorization") auth : String, @Field("longitude") longitude : Double, @Field("latitude") latitude :
    Double, @Field("mobileNumber") mobileNumber : String, @Field("address") address : String) : Call<AddAddressResponseModel>

    @GET("customer/addresses")
    fun getAddress(@Header("Authorization") auth : String) : Call<GetAddressesResponseModel>

    @DELETE("customer/addresses/{id}")
    fun deleteAddress(@Header("Authorization") auth : String, @Path("id") id : String) : Call<DeleteAddressResponseModel>

    @POST("coupin")
    fun getCoupin(@Body requestBody : GetCoupinRequestModel, @Header("Authorization") auth : String)
    : Call<GetCoupinResponseModel>
}

interface GokadaPriceEstimateService{
    @POST("api/developer/v3/order_estimate")
    fun getPriceEstimate(@Body requestBody : GokadaOrderEstimateRequestBody) : Call<GokadaOrderEstimateResponse>
}