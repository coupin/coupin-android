package com.kibou.abisoyeoke_lawal.coupinapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward
import com.kibou.abisoyeoke_lawal.coupinapp.utils.*
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.GetCoupinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_get_coupin.*
import java.util.*

@AndroidEntryPoint
class GetCoupinActivity : AppCompatActivity() {

    private val getCoupinVM : GetCoupinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_coupin)

        getIntentRewards()
        setMerchant()
        setExpiryDate()
        setUpFragmentNavigation()
        getRewardsObject()
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

    private fun getRewardsObject(){
        val rewardObjectString = intent.getStringExtra(rewardObjectsIntent)
        rewardObjectString?.let {
            getCoupinVM.rewardObjectsString.value = it
        }
    }

    private fun setMerchant(){
        val merchantString = intent.getStringExtra(merchantIntent)
        merchantString?.let {
            val merchant = Gson().fromJson(it, Merchant::class.java)
            merchant?.let {
                getCoupinVM.setMerchant(it)
            }
        }
    }

    private fun setExpiryDate(){
        val expiryDateString = intent.getStringExtra(expiryDateIntent)
        expiryDateString?.let {
            getCoupinVM.expiryDateMLD.value = it
        }
    }

    private fun setUpFragmentNavigation(){
        val navHost = get_coupin_nav_host_fragment as NavHostFragment
        val navGraph = navHost.navController.navInflater.inflate(R.navigation.get_coupin_navigation)

        val goToPayment = intent.getBooleanExtra(intentExtraGoToPayment, false)
        if(goToPayment){
            navGraph.startDestination = R.id.checkoutFragment
        }
        navHost.navController.graph = navGraph
    }
}