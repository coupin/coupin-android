package com.kibou.abisoyeoke_lawal.coupinapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressLocation

class AddressLocationDBConverters {
    @TypeConverter
    fun locationToString(location : AddressLocation) : String{
        return Gson().toJson(location)
    }

    @TypeConverter
    fun stringToLocation(location: String) :AddressLocation{
        return Gson().fromJson(location, AddressLocation::class.java)
    }
}