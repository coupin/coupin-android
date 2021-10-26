package com.kibou.abisoyeoke_lawal.coupinapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.PlacesSearchRecyclerClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.PlacesSearchRecyclerResource

/**
 * Created by The Awesome Simileoluwa Aluko on 2020-03-15.
 */
class RVPlacesSearchAdapter(var resource : ArrayList<PlacesSearchRecyclerResource>,
                            val placesSearchRecyclerClickListener: PlacesSearchRecyclerClickListener) :
    RecyclerView.Adapter<RVPlacesSearchAdapter.PlacesSearchViewHolder>() {

    class PlacesSearchViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        val mainText = view.findViewById<TextView>(R.id.main_text)
        val secondaryText = view.findViewById<TextView>(R.id.secondary_text)
        val itemLayout = view.findViewById<ConstraintLayout>(R.id.places_search_item_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesSearchViewHolder {
        return PlacesSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_place_search_result, parent, false))
    }

    override fun getItemCount(): Int {
        return resource.size
    }

    override fun onBindViewHolder(holder: PlacesSearchViewHolder, position: Int) {
        val itemResource = resource[position]
        val mainText = itemResource.mainText
        val secondaryText = itemResource.secondaryText
        val placeId = itemResource.placeId

        holder.mainText.text = mainText
        holder.secondaryText.text = secondaryText
        holder.itemLayout.setOnClickListener {
            placesSearchRecyclerClickListener.onPlacesSearchRecyclerClick(mainText, secondaryText, placeId)
        }
    }

    fun updateViewResource(newResource : ArrayList<PlacesSearchRecyclerResource>){
        this.resource.clear()
        this.resource.addAll(newResource)
        this.notifyDataSetChanged()
    }

}