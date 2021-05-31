package com.kibou.abisoyeoke_lawal.coupinapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVAddressBookAdapter
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.AddressBookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_address_book.*
import org.jetbrains.anko.alert
import java.lang.Exception

@AndroidEntryPoint
class AddressBookActivity : AppCompatActivity(), View.OnClickListener {

    private val addressBookViewModel : AddressBookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_book)
        setUpOnClickListeners()
        getAddress()
    }

    private fun setUpOnClickListeners(){
        add_address.setOnClickListener(this)
        address_book_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            add_address.id -> {
                startActivity(Intent(this@AddressBookActivity, AddAddressActivity::class.java))
            }
            address_book_back.id -> {
                onBackPressed()
            }
        }
    }

    private fun getAddress(){
        try{
            val token = PreferenceMngr.getToken() ?: ""
            addressBookViewModel.getAddresses(token).observe(this, {
                it?.let {
                    when(it.status){
                        Resource.Status.ERROR ->{
                            progress_bar.visibility = View.GONE
                            alert( "Error getting addresses. Please try again later.").show()
                        }
                        Resource.Status.SUCCESS ->{
                            progress_bar.visibility = View.GONE
                            if(it.data != null){
                                val addressesAdapter = RVAddressBookAdapter(it.data.addresses)
                                address_recycler.apply {
                                    adapter = addressesAdapter
                                    layoutManager = LinearLayoutManager(this@AddressBookActivity, LinearLayoutManager.VERTICAL, false)
                                }
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
}