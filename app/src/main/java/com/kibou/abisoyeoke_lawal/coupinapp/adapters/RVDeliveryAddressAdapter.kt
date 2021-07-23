package com.kibou.abisoyeoke_lawal.coupinapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.DeliveryAddressItemClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressResponseModel
import org.jetbrains.anko.find

class RVDeliveryAddressAdapter(private val resource : MutableList<AddressResponseModel>,
                               private val deliveryAddressItemClickListener : DeliveryAddressItemClickListener, var
                               selectedPosition: Int?, private val context : Context)
    : RecyclerView.Adapter<RVDeliveryAddressAdapter.RVDeliveryAddressVH>() {

    class RVDeliveryAddressVH(view : View) : RecyclerView.ViewHolder(view) {
        val addressLayout = view.find<ConstraintLayout>(R.id.delivery_address_layout)
        val address = view.find<TextView>(R.id.address)
        val phoneNumber = view.find<TextView>(R.id.phone_number)
        val baseLayout = view.find<CardView>(R.id.base_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVDeliveryAddressAdapter.RVDeliveryAddressVH {
        return RVDeliveryAddressAdapter.RVDeliveryAddressVH(LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_delivery_address, parent, false))
    }

    override fun onBindViewHolder(holder: RVDeliveryAddressAdapter.RVDeliveryAddressVH, position: Int) {
        val viewResource = resource[position]
        holder.address.text = viewResource.address
        holder.phoneNumber.text = viewResource.mobileNumber
        holder.addressLayout.setOnClickListener {
            deliveryAddressItemClickListener.onAddressClick(viewResource)
        }
        if(selectedPosition == position){
            holder.baseLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
            holder.address.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.phoneNumber.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else {
            holder.baseLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.background))
            holder.address.setTextColor(ContextCompat.getColor(context, R.color.text_color_1))
            holder.phoneNumber.setTextColor(ContextCompat.getColor(context, R.color.text_color_4))
        }
    }

    override fun getItemCount(): Int {
        return resource.size
    }

    fun updateClickedView(addressResponseModel: AddressResponseModel){
        val addressForDeletion = resource.filter { it.id == addressResponseModel.id }[0]
        val position = resource.indexOf(addressForDeletion)
        this.selectedPosition = position

        val tempResource = mutableListOf<AddressResponseModel>()
        tempResource.addAll(resource)
        this.resource.clear()
        this.resource.addAll(tempResource)
        this.notifyDataSetChanged()
    }
}