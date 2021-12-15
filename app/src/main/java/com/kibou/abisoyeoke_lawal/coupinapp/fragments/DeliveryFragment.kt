package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kibou.abisoyeoke_lawal.coupinapp.BuildConfig
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.activities.AddAddressActivity
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVDeliveryAddressAdapter
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.DeliveryAddressItemClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.DropOff
import com.kibou.abisoyeoke_lawal.coupinapp.models.GokadaOrderEstimateRequestBody
import com.kibou.abisoyeoke_lawal.coupinapp.utils.DateTimeUtils
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import com.kibou.abisoyeoke_lawal.coupinapp.utils.setAmountFormat
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.DeliveryViewModel
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.GetCoupinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_address_book.*
import kotlinx.android.synthetic.main.activity_address_book.address_recycler
import kotlinx.android.synthetic.main.activity_address_book.progress_bar
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_delivery.*
import org.jetbrains.anko.toast
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DeliveryFragment : Fragment(), View.OnClickListener, DeliveryAddressItemClickListener {

    private val deliveryViewModel : DeliveryViewModel by viewModels()
    private val getCoupinVM : GetCoupinViewModel by activityViewModels()
    private lateinit var addressAdapter : RVDeliveryAddressAdapter
    private var rewardsCost = 0F

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_delivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpOnClickListeners()
        setUpObservers()
        setUpViews()
    }

    private fun setUpViews(){
        delivery_time.text = delivery_time.text.toString() + " " + getString(R.string.select_an_address)
        delivery_cost.text = getString(R.string.select_an_address)
    }

    private fun setUpObservers() {
        deliveryViewModel.getAddressesFromDB().observe(viewLifecycleOwner, {
            it?.let {
                addressAdapter = RVDeliveryAddressAdapter(it.toMutableList(), this@DeliveryFragment, null, requireContext())
                address_recycler.adapter = addressAdapter
            }
        })

        try{
            val token = PreferenceManager.getToken() ?: ""
            deliveryViewModel.getAddressesFromNetwork(token).observe(viewLifecycleOwner, {
                it?.let {
                    when(it.status){
                        Resource.Status.ERROR ->{
                            progress_bar.visibility = View.GONE
                            Toast.makeText(requireContext(),  "Error getting addresses. Please try again later.", Toast.LENGTH_SHORT)
                                    .show()
                        }
                        Resource.Status.SUCCESS ->{
                            progress_bar.visibility = View.GONE
                            if(it.data != null){
                                deliveryViewModel.addAddressesToDB(it.data.addresses)
                            }
                        }
                        Resource.Status.LOADING ->{ progress_bar.visibility = View.VISIBLE }
                    }
                }
            })
        }catch (e : Exception){
            e.printStackTrace()
        }

        getCoupinVM.selectedCoupinsLD.observe(viewLifecycleOwner, {
            it?.let {
                val rewardCostSum = it.map {
                    it.price.newPrice * it.selectedQuantity
                }.sum()
                items_cost.text = "₦ ${setAmountFormat(rewardCostSum)}"
                total_cost.text = "₦ ${setAmountFormat(rewardCostSum)}"
                rewardsCost = rewardCostSum
            }
        })
    }

    private fun setUpOnClickListeners() {
        add_new_address.setOnClickListener(this)
        make_payment_btn.setOnClickListener(this)
        delivery_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            add_new_address.id -> {
                startActivity(Intent(requireContext(), AddAddressActivity::class.java))
            }
            make_payment_btn.id -> {
                if(::addressAdapter.isInitialized){
                    if(addressAdapter.selectedPosition != null){
                        if(getCoupinVM.deliveryPriceLD.value != null){
                            getCoupinVM.isDeliverableMLD.value = true
                            val action = DeliveryFragmentDirections.actionDeliveryFragmentToCheckoutFragment()
                            findNavController().navigate(action)
                        }else {
                            requireActivity().toast("Kwik delivery is unavailable at this time")
                        }
                    }else{
                        requireActivity().toast("Select an address")
                    }
                }
            }
            delivery_back.id -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onAddressClick(addressModel: AddressResponseModel) {
        if(::addressAdapter.isInitialized){
            addressAdapter.updateClickedView(addressModel)
//            val deliveryLatitude = addressModel.location.latitude
//            val deliveryLongitude = addressModel.location.longitude
            val deliveryAddressId = addressModel.id
            val merchantId = getCoupinVM.merchantLD.value?.id

            getCoupinVM.addressIdMLD.value = addressModel.id

            if(merchantId != null){
                getKwikPriceEstimate(merchantId, deliveryAddressId, rewardsCost);
            }

            // TODO: Uncomment GoKaDa Implementation when it's working
//            addressAdapter.updateClickedView(addressModel)
//            val deliveryLatitude = addressModel.location.latitude
//            val deliveryLongitude = addressModel.location.longitude
//            val deliveryAddress = addressModel.address
//
//            getCoupinVM.addressIdMLD.value = addressModel.id
//
//            if(deliveryLatitude != null && deliveryLongitude != null){
//                getPriceEstimate(deliveryLatitude.toString(), deliveryLongitude.toString(), deliveryAddress)
//            }
        }
    }

    private fun getKwikPriceEstimate(merchantId : String, deliveryAddressId : String, totalCost : Float){
        deliveryViewModel.getKwikDeliveryEstimate(merchantId, deliveryAddressId, totalCost).observe(this, {
            it?.let {
                when(it.status){
                    Resource.Status.SUCCESS -> {
                        progress_bar.visibility = View.GONE
                        it.data?.let {
                            val deliveryCost = it.data?.estimatedCost ?: 0F
                            delivery_cost.text = "\u20A6 ${setAmountFormat(deliveryCost)}"

                            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            val deliveryDateTime = DateTimeUtils.convertZString(it.data?.deliveryTime)
                            val diff: Long = deliveryDateTime.time - Calendar.getInstance().time.time;
                            val minutes = (diff / 1000) / 60
                            delivery_time.text = getString(R.string.estimated_time_of_delivery) + minutes + "mins"
//                            delivery_time.text = getString(R.string.estimated_time_of_delivery) + " " + it.time + "mins"

                            val totalCostString = setAmountFormat(deliveryCost + rewardsCost)
                            total_cost.text = "₦ $totalCostString"

                            getCoupinVM.setDeliveryPrice(deliveryCost.toInt())
                        }
                    }
                    Resource.Status.ERROR -> {
                        getCoupinVM.setDeliveryPrice(null)
                        progress_bar.visibility = View.GONE
                        requireContext().toast("Error getting delivery price estimate. Please try again later.").show()
                    }
                    Resource.Status.LOADING -> progress_bar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun getPriceEstimate(deliveryLatitude : String, deliveryLongitude : String, deliveryAddress : String){
        val merchant = getCoupinVM.merchantLD.value
        val merchantLatitude = merchant?.location?.latitude.toString()
        val merchantLongitude = merchant?.location?.longitude.toString()
        val merchantAddress = merchant?.address ?: ""

        val dropOff = DropOff(deliveryAddress, deliveryLatitude, deliveryLongitude)

        val gokadaOrderEstimateRequestBody = GokadaOrderEstimateRequestBody(BuildConfig.GOKADA_DELIVERY_API_KEY, merchantAddress,
            merchantLatitude, merchantLongitude, listOf(dropOff))

        deliveryViewModel.getDeliveryEstimate(gokadaOrderEstimateRequestBody).observe(this, {
            it?.let {
                when(it.status){
                    Resource.Status.SUCCESS -> {
                        progress_bar.visibility = View.GONE
                        it.data?.let {
                            val deliveryCost = it.fare ?: 0
                            delivery_cost.text = "\u20A6 ${setAmountFormat(deliveryCost)}"
                            delivery_time.text = getString(R.string.estimated_time_of_delivery) + " " + it.time + "mins"

                            val totalCostString = setAmountFormat(deliveryCost + rewardsCost)
                            total_cost.text = "₦ $totalCostString"

                            getCoupinVM.setDeliveryPrice(deliveryCost)
                        }
                    }
                    Resource.Status.ERROR -> {
                        getCoupinVM.setDeliveryPrice(null)
                        progress_bar.visibility = View.GONE
                        requireContext().toast("Error getting delivery price estimate. Please try again later.").show()
                    }
                    Resource.Status.LOADING -> progress_bar.visibility = View.VISIBLE
                }
            }
        })
    }
}