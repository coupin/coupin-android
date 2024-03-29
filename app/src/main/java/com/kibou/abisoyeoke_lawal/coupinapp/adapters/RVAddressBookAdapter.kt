package com.kibou.abisoyeoke_lawal.coupinapp.adapters

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.AddressBookItemClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressResponseModel
import org.jetbrains.anko.find

class RVAddressBookAdapter(private val resource : List<AddressResponseModel>, private val addressBookItemClickListener :
AddressBookItemClickListener) :
        RecyclerView.Adapter<RVAddressBookAdapter.RVAddressBookVH>() {

    class RVAddressBookVH(view : View) : RecyclerView.ViewHolder(view) {
        val address = view.find<TextView>(R.id.address)
        val phoneNumber = view.find<TextView>(R.id.phone_number)
        val cancelBtn= view.find<ImageButton>(R.id.cancel_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVAddressBookVH {
        return RVAddressBookVH(LayoutInflater.from(parent.context).inflate(R.layout.item_address_book, parent, false))
    }

    override fun onBindViewHolder(holder: RVAddressBookVH, position: Int) {
        val viewResource = resource[position]
        holder.address.text = viewResource.address
        holder.phoneNumber.text = viewResource.mobileNumber
        holder.cancelBtn.setOnClickListener {
            addressBookItemClickListener.onAddressCancelClick(viewResource)
        }
    }

    override fun getItemCount(): Int {
        return resource.size
    }
}