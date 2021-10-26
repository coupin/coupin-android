package com.kibou.abisoyeoke_lawal.coupinapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressResponseModel

@Database(entities = [AddressResponseModel::class], version = 1, exportSchema = false)
@TypeConverters(AddressLocationDBConverters::class)
abstract class CoupinDatabase : RoomDatabase(){
    abstract fun addressDao() : AddressDAO

    companion object {
        @Volatile
        private var INSTANCE: CoupinDatabase? = null

        fun getDatabase(context: Context): CoupinDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        CoupinDatabase::class.java,
                        "coupin_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}