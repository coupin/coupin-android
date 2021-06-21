package com.kibou.abisoyeoke_lawal.coupinapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.gson.Gson
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward
import com.kibou.abisoyeoke_lawal.coupinapp.utils.rewardsIntent
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.GetCoupinViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GetCoupinActivity : AppCompatActivity() {

    private val getCoupinVM : GetCoupinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_coupin)

        getIntentRewards()
    }

    private fun getIntentRewards(){
        val rewardsString = intent.getStringExtra(rewardsIntent)
        rewardsString?.let{
            val rewardsArray= Gson().fromJson(it, Array<Reward>::class.java)
            rewardsArray?.let {
                getCoupinVM.setSelectedCoupins(it)
            }
        }
    }
}