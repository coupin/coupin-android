package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVReviewSelectionDelivery
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVReviewSelectionPickup
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ReviewSelectionCancelClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardV2
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
        setUpAdapters()
        setUpRewardsViews()
    }

    private fun setUpAdapters(){
        pickupRecyclerAdapter = RVReviewSelectionPickup(mutableListOf(), this)
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
            review_selection_proceed_btn.id -> onProceedBtnClicked()
        }
    }

    private fun setUpRewardsViews(){
        reviewSelectionViewModel.selectedCoupinsLD.observe(viewLifecycleOwner, {
            it?.let {
                val pickupRewards = it.filter { !it.isDelivery }
                val deliveryRewards = it.filter {it.isDelivery}
                setPickupAndDeliveryViews(pickupRewards, deliveryRewards)
                setProceedBtnText(deliveryRewards, pickupRewards)
            }
        })
    }

    private fun setPickupAndDeliveryViews(pickupRewards : List<RewardV2>, deliveryRewards : List<RewardV2>){
        setResourceForPickupAdapter(pickupRewards)
        setResourceForDeliverableAdapter(deliveryRewards)
        setProceedBtnText(deliveryRewards, pickupRewards)
    }

    private fun setResourceForDeliverableAdapter(deliveryRewards : List<RewardV2>){
        if(::deliveryRecyclerAdapter.isInitialized){
            if(deliveryRewards.isEmpty()){
                deliverable_items_title.visibility = View.GONE
                deliverable_recycler.visibility = View.GONE
                review_selection_message.visibility = View.GONE
            }else{
                deliverable_items_title.visibility = View.VISIBLE
                deliverable_recycler.visibility = View.VISIBLE
                review_selection_message.visibility = View.VISIBLE
            }
            deliveryRecyclerAdapter.setResource(deliveryRewards)
        }
    }

    private fun setResourceForPickupAdapter(pickupRewards: List<RewardV2>) {
        if(::pickupRecyclerAdapter.isInitialized){
            if(pickupRewards.isEmpty()){
                pickup_items_title.visibility = View.GONE
                pickup_recycler.visibility = View.GONE
                review_selection_message.visibility = View.GONE
            }else{
                pickup_items_title.visibility = View.VISIBLE
                pickup_recycler.visibility = View.VISIBLE
                review_selection_message.visibility = View.VISIBLE
            }
            pickupRecyclerAdapter.setResource(pickupRewards)
        }
    }

    override fun onCancelClick(reward: RewardV2) {
        val newRewardsList = reviewSelectionViewModel.removeCoupin(reward)
        newRewardsList?.let{
            val deliveryRewards = it.filter {it.isDelivery}
            val pickupRewards = it.filter { !it.isDelivery }
            setResourceForDeliverableAdapter(deliveryRewards)
            setResourceForPickupAdapter(pickupRewards)
            setProceedBtnText(deliveryRewards, pickupRewards)
        }
    }

    private fun setProceedBtnText(deliveryRewards : List<RewardV2>, pickupRewards: List<RewardV2>){
        if(deliveryRewards.isNotEmpty() && pickupRewards.isNotEmpty()){
            review_selection_proceed_btn.text = getString(R.string.change_all_to_pickup)
            empty_cart_layout.visibility = View.GONE
        }else if(deliveryRewards.isEmpty() && pickupRewards.isNotEmpty()){
            review_selection_message.visibility = View.GONE
            review_selection_proceed_btn.text = getString(R.string.proceed)
            empty_cart_layout.visibility = View.GONE
        }else if (deliveryRewards.isEmpty() && pickupRewards.isEmpty()){
            review_selection_proceed_btn.isEnabled = false
            empty_cart_layout.visibility = View.VISIBLE
        }else if(deliveryRewards.isNotEmpty() && pickupRewards.isEmpty()){
            review_selection_message.visibility = View.GONE
            review_selection_proceed_btn.text = getString(R.string.proceed)
            empty_cart_layout.visibility = View.GONE
        }
    }

    private fun onProceedBtnClicked(){
        when(review_selection_proceed_btn.text.toString()){
            getString(R.string.change_all_to_pickup) -> {
                reviewSelectionViewModel.selectedCoupinsLD.value?.forEach {
                    it.isDelivery = false
                }
                setUpRewardsViews()
            }
            getString(R.string.proceed) -> {
                val rewards =  reviewSelectionViewModel.selectedCoupinsLD.value
                rewards?.let {
                    val deliveryRewards = it.filter { it.isDelivery }
                    if(deliveryRewards.isEmpty()){
                        reviewSelectionViewModel.isDeliverableMLD.value = false
                        reviewSelectionViewModel.addressIdMLD.value = ""
                        val action = ReviewSelectionFragmentDirections.actionReviewSelectionFragmentToCheckoutFragment()
                        findNavController().navigate(action)
                    }else{
                        reviewSelectionViewModel.isDeliverableMLD.value = true
                        val action = ReviewSelectionFragmentDirections.actionReviewSelectionFragmentToDeliveryFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }
}








