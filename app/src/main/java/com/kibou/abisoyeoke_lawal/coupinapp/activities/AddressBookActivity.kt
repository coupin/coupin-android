package com.kibou.abisoyeoke_lawal.coupinapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVAddressBookAdapter
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.AddressBookItemClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.AddressBookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_address_book.*
import kotlinx.android.synthetic.main.dialog_delete_address.*
import kotlinx.android.synthetic.main.item_address_book.*
import org.jetbrains.anko.find
import java.lang.Exception

@AndroidEntryPoint
class AddressBookActivity : AppCompatActivity(), View.OnClickListener, AddressBookItemClickListener {

    private val addressBookViewModel : AddressBookViewModel by viewModels()
    private lateinit var addressesAdapter : RVAddressBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_book)

        setUpOnClickListeners()
        getAddressFromNetwork()
        setUpObservables()
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

    private fun setUpObservables(){
        addressBookViewModel.getAddressesFromDB().observe(this, {
            it?.let {
                addressesAdapter = RVAddressBookAdapter(it, this@AddressBookActivity)
                address_recycler.apply {
                    adapter = addressesAdapter
                    layoutManager = LinearLayoutManager(this@AddressBookActivity, LinearLayoutManager.VERTICAL, false)
                }
            }
        })
    }

    private fun getAddressFromNetwork(){
        try{
            val token = PreferenceManager.getToken() ?: ""
            addressBookViewModel.getAddressesFromNetwork(token).observe(this, {
                it?.let {
                    when(it.status){
                        Resource.Status.ERROR ->{
                            progress_bar.visibility = View.GONE
                            Toast.makeText(this@AddressBookActivity,  "Error getting addresses. Please try again later.", Toast.LENGTH_SHORT)
                                    .show()
                        }
                        Resource.Status.SUCCESS ->{
                            progress_bar.visibility = View.GONE
                            if(it.data != null){
                                addressBookViewModel.addAddressesToDB(it.data.addresses)
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

    override fun onAddressCancelClick(addressModel: AddressResponseModel) {
        val addressDeleteView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_address, null)
        val deleteDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(addressDeleteView)
        addressDeleteView.find<Button>(R.id.delete).setOnClickListener {
            performDelete(addressModel)
            deleteDialog.dismiss()
        }
        addressDeleteView.find<Button>(R.id.cancel).setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

    private fun performDelete(addressModel : AddressResponseModel){
        try{
            val token = PreferenceManager.getToken() ?: ""
            addressBookViewModel.deleteAddressFromNetwork(token, addressModel.id).observe(this, {
                it?.let {
                    when(it.status){
                        Resource.Status.ERROR ->{
                            progress_bar.visibility = View.GONE
                            Toast.makeText(this@AddressBookActivity, "Error deleting addresses. Please try again later.",
                                    Toast.LENGTH_SHORT).show()
                        }
                        Resource.Status.SUCCESS ->{
                            progress_bar.visibility = View.GONE
                            if(it.data != null){
                                Toast.makeText(this@AddressBookActivity,"Address deleted successfully", Toast.LENGTH_SHORT).show()
                                addressBookViewModel.deleteAddressFromDB(addressModel)
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