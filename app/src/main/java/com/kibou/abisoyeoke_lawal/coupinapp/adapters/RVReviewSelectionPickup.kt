package com.kibou.abisoyeoke_lawal.coupinapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class RVReviewSelectionPickup(private val resource : MutableList<Reward>) : RecyclerView.Adapter<RVReviewSelectionPickup
.RVReviewSelectionPickupVH>() {

    class RVReviewSelectionPickupVH(view : View) : RecyclerView.ViewHolder(view) {
        val topSectionTitle = view.find<TextView>(R.id.top_section_title)
        val expiryDate = view.find<TextView>(R.id.expiry_date)
        val fullPrice = view.find<TextView>(R.id.full_price)
        val discountedPrice = view.find<TextView>(R.id.discounted_price)
        val discountPercent = view.find<TextView>(R.id.discount_percent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVReviewSelectionPickupVH {
        return RVReviewSelectionPickupVH(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_review_pickup, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RVReviewSelectionPickupVH, position: Int) {
        val viewResource = resource[position]
        holder.apply {
            topSectionTitle.text = viewResource.title

            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            expiryDate.text = simpleDateFormat.format(viewResource.expires)

            fullPrice.text =  "\u20A6 ${viewResource.oldPrice}"
            fullPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG

            discountedPrice.text = "\u20A6 ${viewResource.newPrice}"

            val discount = ((viewResource.oldPrice - viewResource.newPrice) / viewResource.oldPrice * 100).toInt()
            discountPercent.text = "-$discount%"
        }
    }

    override fun getItemCount(): Int {
        return resource.size
    }

    fun setResource(resource : List<Reward>){
        this.resource.clear()
        this.resource.addAll(resource)
        this.notifyDataSetChanged()
    }
}