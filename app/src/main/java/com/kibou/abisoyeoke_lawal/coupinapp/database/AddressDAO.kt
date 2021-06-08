package com.kibou.abisoyeoke_lawal.coupinapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressResponseModel
import kotlin.coroutines.RestrictsSuspension

@Dao
interface AddressDAO {

    @Query("SELECT * from AddressResponseModel")
    fun getAddresses() : LiveData<List<AddressResponseModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address : AddressResponseModel)

    @Delete
    suspend fun deleteAddress(address: AddressResponseModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses : List<AddressResponseModel>)
}