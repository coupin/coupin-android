package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVPopUpAdapter
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnSelect
import com.kibou.abisoyeoke_lawal.coupinapp.utils.setAmountFormat
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.GetCoupinViewModel
import kotlinx.android.synthetic.main.fragment_cart.*


class CartFragment : Fragment(), View.OnClickListener, MyOnSelect {

    private val cartViewModel : GetCoupinViewModel by activityViewModels()
    private val logTag = "CartFragment"
    private lateinit var rvPopUpAdapter : RVPopUpAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpRecycler()
        cart_back.setOnClickListener(this)
        cart_proceed_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.cart_back -> requireActivity().onBackPressed()
            R.id.cart_proceed_btn -> {
                if(::rvPopUpAdapter.isInitialized && rvPopUpAdapter.rewards.isNotEmpty()){
                    val action = CartFragmentDirections.actionCartFragmentToDeliveryMethodFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun getTotalAmount(): Double {
        val rewards = cartViewModel.selectedCoupinsLD.value
        val rewardsQuantity = cartViewModel.rewardQuantityMLD.value ?: hashMapOf()
        val rewardsPrice = rewards?.map {
            val quantity = rewardsQuantity[it.id]
            if (quantity != null) {
                it.newPrice * quantity;
            } else {
                it.newPrice
            }
        }?.sum() ?: 0F

        val deliveryPrice = cartViewModel.deliveryPriceLD.value ?: 0
        return (rewardsPrice + deliveryPrice).toDouble()
    }


    private fun setUpRecycler(){
        cartViewModel.selectedCoupinsLD.observe(viewLifecycleOwner, {
            it?.let {
                rvPopUpAdapter = RVPopUpAdapter(it, requireContext(), this)
                val blackList = cartViewModel.tempBlackListMLD.value
                rvPopUpAdapter.blacklist = blackList
                cart_recycler.adapter = rvPopUpAdapter
                total_cost.text = "₦${setAmountFormat(getTotalAmount())}"

                if (it.isEmpty()){
                    empty_cart_layout.visibility = View.VISIBLE
                    cart_recycler.visibility = View.GONE
                }else {
                    empty_cart_layout.visibility = View.GONE
                    cart_recycler.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onSelect(selected: Boolean, index: Int) {
        if (index == -1) {
            Toast.makeText(requireContext(), "Sorry this reward can only be used once. ", Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onSelect(selected: Boolean, index: Int, quantity: Int) {
        if(!selected){
            val rewards = cartViewModel.selectedCoupinsLD.value
            rewards?.let {
                it.removeAt(index)
                cartViewModel.setSelectedCoupins(it.toTypedArray())
            }
        }
    }
}