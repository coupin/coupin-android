package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.GetCoupinViewModel
import kotlinx.android.synthetic.main.fragment_delivery_method.*
import kotlinx.android.synthetic.main.view_gokada_delivery_method.*
import kotlinx.android.synthetic.main.view_pickup_delivery_method.*
import org.jetbrains.anko.toast

class DeliveryMethodFragment : Fragment(), View.OnClickListener {

    private val deliveryMethodVM : GetCoupinViewModel by activityViewModels()
    private val logTag = "DeliveryMethodAct"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_delivery_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpOnClickListeners()
        setUpObservables()
    }

    private fun setUpOnClickListeners(){
        pickup_delivery_method_layout.setOnClickListener(this)
        gokada_delivery_method_layout.setOnClickListener(this)
        proceed_btn.setOnClickListener(this)
        delivery_method_back.setOnClickListener(this)
        pickup_btn.setOnClickListener(this)
        delivery_btn.setOnClickListener(this)
    }

    private fun setUpObservables(){
        deliveryMethodVM.selectedDeliveryMethod.observe(viewLifecycleOwner, {
            it?.let {
                when(it){
                    R.id.pickup_delivery_method_layout -> {
                        pickup_delivery_method_layout.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_selected_delivery_method)
                        gokada_delivery_method_layout.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_unselected_delivery_method)
                        gokada_logo_text.setImageResource(R.drawable.kwik_banner_gray)
                    }
                    R.id.gokada_delivery_method_layout -> {
                        pickup_delivery_method_layout.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_unselected_delivery_method)
                        gokada_delivery_method_layout.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_selected_delivery_method)
                        gokada_logo_text.setImageResource(R.drawable.kwik_banner)
                    }
                }
            }
        })

        deliveryMethodVM.selectedCoupinsLD.observe(viewLifecycleOwner, {
            it?.let {
                val deliveryStatuses = it.map { it.isDelivery }
                if(!deliveryStatuses.contains(true)){
                    deliveryMethodVM.selectedDeliveryMethod.value = R.id.pickup_delivery_method_layout
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            pickup_delivery_method_layout.id, pickup_btn.id -> {
                deliveryMethodVM.selectedDeliveryMethod.value = R.id.pickup_delivery_method_layout
            }
            gokada_delivery_method_layout.id, delivery_btn.id -> {
                //TODO: Put back
//                val deliveryStatuses = deliveryMethodVM.selectedCoupinsLD.value?.map { it.isDelivery }
//                deliveryStatuses?.let {
//                    if(!deliveryStatuses.contains(true)){
//                        deliveryMethodVM.selectedDeliveryMethod.value = R.id.pickup_delivery_method_layout
//                        requireContext().toast("No item in your reward(s) is deliverable")
//                    }else {
//                        deliveryMethodVM.selectedDeliveryMethod.value = R.id.gokada_delivery_method_layout
//                    }
//                }
                requireActivity().toast("We'll be launching Kwik Delivery soon. Just preparing everything specially for you!")
            }
            proceed_btn.id -> proceedToNextPage()
            delivery_method_back.id -> requireActivity().onBackPressed()
        }
    }

    private fun proceedToNextPage(){
        val selectedDeliveryMethod = deliveryMethodVM.selectedDeliveryMethod.value
        if(selectedDeliveryMethod == null){
            requireContext().toast("Select a delivery method")
            return
        }

        if(selectedDeliveryMethod == R.id.pickup_delivery_method_layout){
            deliveryMethodVM.isDeliverableMLD.value = false
            deliveryMethodVM.addressIdMLD.value = ""
            val action = DeliveryMethodFragmentDirections.actionDeliveryMethodFragmentToCheckoutFragment()
            findNavController().navigate(action)
            return
        }

        if(selectedDeliveryMethod == R.id.gokada_delivery_method_layout){
            val rewards = deliveryMethodVM.selectedCoupinsLD.value
            rewards?.let {
                val deliveryStatuses = it.map { it.isDelivery }
                if(deliveryStatuses.contains(true) && deliveryStatuses.contains(false)){
                    val action = DeliveryMethodFragmentDirections.actionDeliveryMethodFragmentToReviewSelectionFragment()
                    findNavController().navigate(action)
                    return
                }

                if (!deliveryStatuses.contains(false)){
                    val action = DeliveryMethodFragmentDirections.actionDeliveryMethodFragmentToDeliveryFragment()
                    findNavController().navigate(action)
                    return
                }
            }
        }
    }
}

















