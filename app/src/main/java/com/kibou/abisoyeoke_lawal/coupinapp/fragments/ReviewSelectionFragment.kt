package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVReviewSelectionDelivery
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVReviewSelectionPickup
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ReviewSelectionCancelClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.GetCoupinViewModel
import kotlinx.android.synthetic.main.fragment_review_selection.*

class ReviewSelectionFragment : Fragment(), View.OnClickListener, ReviewSelectionCancelClickListener {

    private val reviewSelectionViewModel : GetCoupinViewModel by activityViewModels()
    private lateinit var pickupRecyclerAdapter : RVReviewSelectionPickup
    private lateinit var deliveryRecyclerAdapter : RVReviewSelectionDelivery

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpOnClickListeners()
        setUpRewardsViews()
        setUpAdapters()
    }

    private fun setUpAdapters(){
        pickupRecyclerAdapter = RVReviewSelectionPickup(mutableListOf())
        pickup_recycler.adapter = pickupRecyclerAdapter

        deliveryRecyclerAdapter = RVReviewSelectionDelivery(mutableListOf(), this)
        deliverable_recycler.adapter = deliveryRecyclerAdapter
    }

    private fun setUpOnClickListeners(){
        review_selection_back.setOnClickListener(this)
        review_selection_proceed_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            review_selection_back.id -> requireActivity().onBackPressed()
            review_selection_proceed_btn.id -> { }
        }
    }

    private fun setUpRewardsViews(){
        reviewSelectionViewModel.selectedCoupinsLD.observe(viewLifecycleOwner, {
            it?.let {
                val pickupRewards = it.filter { !it.isDelivery }
                val deliveryRewards = it.filter {it.isDelivery}
                if(::pickupRecyclerAdapter.isInitialized){
                    pickupRecyclerAdapter.setResource(pickupRewards)
                }
                if(::deliveryRecyclerAdapter.isInitialized){
                    deliveryRecyclerAdapter.setResource(deliveryRewards)
                }
            }
        })
    }

    override fun onCancelClick(reward: Reward) {
        val newRewardsList = reviewSelectionViewModel.removeCoupin(reward)
        newRewardsList?.let{
            val deliveryRewards = it.filter {it.isDelivery}
            if(::deliveryRecyclerAdapter.isInitialized){
                deliveryRecyclerAdapter.setResource(deliveryRewards)
            }
        }
    }
}