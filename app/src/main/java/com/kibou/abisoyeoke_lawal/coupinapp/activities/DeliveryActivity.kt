package com.kibou.abisoyeoke_lawal.coupinapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVDeliveryAddressAdapter
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.DeliveryAddressItemClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.AddAddressViewModel
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.DeliveryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_address_book.*
import kotlinx.android.synthetic.main.activity_delivery.*
import kotlinx.android.synthetic.main.activity_delivery.address_recycler
import kotlinx.android.synthetic.main.activity_delivery.progress_bar
import java.lang.Exception

@AndroidEntryPoint
class DeliveryActivity : AppCompatActivity(), View.OnClickListener, DeliveryAddressItemClickListener {

    private val deliveryViewModel : DeliveryViewModel by viewModels()
    private lateinit var addressAdapter : RVDeliveryAddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery)
        setUpOnClickListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        deliveryViewModel.getAddressesFromDB().observe(this, {
            it?.let {
                addressAdapter = RVDeliveryAddressAdapter(it.toMutableList(), this@DeliveryActivity, 0, this@DeliveryActivity)
                address_recycler.adapter = addressAdapter
            }
        })

        try{
            val token = PreferenceMngr.getToken() ?: ""
            deliveryViewModel.getAddressesFromNetwork(token).observe(this, {
                it?.let {
                    when(it.status){
                        Resource.Status.ERROR ->{
                            progress_bar.visibility = View.GONE
                            Toast.makeText(this@DeliveryActivity,  "Error getting addresses. Please try again later.", Toast.LENGTH_SHORT)
                                    .show()
                        }
                        Resource.Status.SUCCESS ->{
                            progress_bar.visibility = View.GONE
                            if(it.data != null){
                                deliveryViewModel.addAddressesToDB(it.data.addresses)
                            }
                        }
                        Resource.Status.LOADING ->{ progress_bar.visibility = View.VISIBLE }
                    }
                }
            })
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun setUpOnClickListeners() {
        add_new_address.setOnClickListener(this)
        make_payment_btn.setOnClickListener(this)
        delivery_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            add_new_address.id -> {
                startActivity(Intent(this@DeliveryActivity, AddAddressActivity::class.java))
            }
            make_payment_btn.id -> {
                val intent = Intent(this@DeliveryActivity, CheckoutActivity::class.java)
                startActivity(intent)
            }
            delivery_back.id -> {
                onBackPressed()
            }
        }
    }

    override fun onAddressClick(addressModel: AddressResponseModel) {
        if(::addressAdapter.isInitialized){
            addressAdapter.updateClickedView(addressModel)
        }
    }


}