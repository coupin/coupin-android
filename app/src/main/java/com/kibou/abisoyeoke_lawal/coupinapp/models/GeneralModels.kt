package com.kibou.abisoyeoke_lawal.coupinapp.models


/* PLACE SEARCH*/
/* PLACE SEARCH*/
data class PlacesSearchRecyclerResource(val mainText : String?, val secondaryText: String?, val placeId: String?)
data class PlacesSearchResponseModel (val predictions: List<Prediction>, val status: String)
data class Prediction (
        val description: String,
        val id: String,
        val matched_substrings: List<MatchedSubstring>,
        val place_id: String,
        val reference: String,
        val structured_formatting: StructuredFormatting,
        val terms: List<Term>,
        val types: List<String>
)
data class MatchedSubstring (val length: Int, val offset: Int)
data class StructuredFormatting (
        val main_text: String,
        val main_text_matched_substrings: List<MainTextMatchedSubstring>,
        val secondary_text: String?
)
data class Term (val offset: Int, val value: String)
data class MainTextMatchedSubstring (val length: Int, val offset: Int)
data class ReverseGeocodingResponseModel (val plus_code : Plus_code, val results : List<Results>, val status : String)
data class Results (val address_components : List<Address_components>, val formatted_address : String,
                    val geometry : Geometry, val place_id : String, val plus_code : Plus_code,
                    val types : List<String>)
data class Plus_code (val compound_code : String, val global_code : String)
data class Address_components (val long_name : String, val short_name : String, val types : List<String>)
data class Geometry (val location : Location, val location_type : String, val viewport : Viewport)
data class Viewport (val northeast : Northeast, val southwest : Southwest)
data class Location (val lat : Double, val lng : Double)
data class Southwest (val lat : Double, val lng : Double)
data class Northeast (val lat : Double, val lng : Double)



/* USER ADDRESS*/
/* USER ADDRESS*/
data class AddressModel(val address : String, val longitude : Double, val latitude : Double, val mobileNumber : String, val token : String)
data class AddressResponseModel(val id : String?, val address : String?, val location : AddressLocation?, val mobileNumber : String?, val owner : String?)
data class AddressLocation(val longitude: Double?, val latitude : Double?)
data class GetAddressesResponseModel(val addresses : List<AddressResponseModel>)
data class AddAddressResponseModel(val message : String, val address : AddressResponseModel?)


/* API ERROR*/
/* API ERROR*/
data class APIError(val error : Boolean, val message : String, val data : Any? = null)
