package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.Meta
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.google.android.play.core.review.ReviewManagerFactory
import com.kibou.abisoyeoke_lawal.coupinapp.BuildConfig
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.activities.CoupinActivity
import com.kibou.abisoyeoke_lawal.coupinapp.models.GetCoupinRequestModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.GetCoupinResponseModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem
import com.kibou.abisoyeoke_lawal.coupinapp.utils.*
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.GetCoupinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_checkout.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class CheckoutFragment : Fragment(), View.OnClickListener {

    private val checkoutViewModel : GetCoupinViewModel by activityViewModels()
    private val logTag = "CheckoutFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpOnClickListeners()
        setUpObservables()
        fillInUserDetails()
    }
    
    private fun fillInUserDetails(){
        PreferenceMngr.setContext(requireContext())
        val userString = PreferenceMngr.getUser()
        userString?.let{
            val userEmail = JSONObject(it).getString("email")
            val temp: String = JSONObject(it).getString("name")
            val names = temp.split(" ".toRegex()).toTypedArray()
            email_input.setText(userEmail)
            first_name_input.setText(names[0])
            last_name_input.setText(names[1])
        }
    }

    private fun setUpOnClickListeners(){
        pay_btn.setOnClickListener(this)
        checkout_back.setOnClickListener(this)
    }

    private fun setUpObservables(){
        checkoutViewModel.selectedCoupinsLD.observe(viewLifecycleOwner, {
            it?.let {
                val rewardsQuantity = checkoutViewModel.rewardQuantityMLD.value ?: hashMapOf()
                val rewardsPrice = it.map {
                    val quantity = rewardsQuantity[it.id]
                    if(quantity != null){
                        it.newPrice * quantity;
                    }else {
                        it.newPrice
                    }
                }.sum()
                val deliveryPrice = checkoutViewModel.deliveryPriceLD.value ?: 0
                val totalAmount = rewardsPrice + deliveryPrice
                pay_btn.text = "Pay ₦${setAmountFormat(totalAmount)}"
            }
        })
    }

    private fun payWithFlutterwave(paymentData: PaymentData, getCoupinResponseModel: GetCoupinResponseModel){
        val rewardsCost = checkoutViewModel.selectedCoupinsLD.value?.map { it.newPrice }?.sum() ?: 0F
        val deliveryCost = checkoutViewModel.deliveryPriceLD.value ?: 0
        val totalCost = (rewardsCost + deliveryCost).toDouble()
        val reference = getCoupinResponseModel.data?.reference ?: ""
        val coupinId = getCoupinResponseModel.data?.booking?._id ?: ""

        Log.d(logTag, "reference : $reference")
        Log.d(logTag, "coupinId : $coupinId")

        RaveUiManager(this)
            .setAmount(totalCost)
            .setCurrency(currency)
            .setEmail(paymentData.email)
            .setfName(paymentData.firstName)
            .setlName(paymentData.lastName)
            .setPublicKey(BuildConfig.COUPIN_FLUTTERWAVE_PUBLIC_KEY)
            .setEncryptionKey(BuildConfig.COUPIN_ENCRYPTION_KEY)
            .setTxRef(reference)
            .acceptAccountPayments(true)
            .acceptCardPayments(true)
            .allowSaveCardFeature(true)
            .shouldDisplayFee(true)
            .setMeta(listOf(Meta(paymentTypeText, coupinText), Meta(coupinIdText, coupinId)))
            .withTheme(R.style.RaveCustomTheme)
            .initialize()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            pay_btn.id -> prepareForPayment()
            checkout_back.id -> requireActivity().onBackPressed()
        }
    }

    private fun prepareForPayment(){
        val email = email_input.text.toString().trim()
        val firstName = first_name_input.text.toString().trim()
        val lastName = last_name_input.text.toString().trim()

        if(!isEmailValid(email)){
            requireContext().toast("Enter a valid email address")
            return
        }

        if(firstName.isEmpty()){
            requireContext().toast("Enter a valid first name.")
            return
        }

        if(lastName.isEmpty()){
            requireContext().toast("Enter a valid last name.")
            return
        }
        val paymentData = PaymentData(email, firstName, lastName)
        getCoupin(paymentData)
    }

    fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private data class PaymentData(val email : String, val firstName : String, val lastName : String)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE) {

            val message = data?.getStringExtra("response")

            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                requireActivity().longToast("Payment Successful")
                setPaymentBtn(false)
                Log.d(logTag, "payment success : $message")
                checkoutViewModel.coupinResponseModelMLD.value?.let {
                    reviewApplication(it)
                }
            }

            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                requireActivity().toast("Payment error")
                setPaymentBtn(false)
                Log.d(logTag, "payment error : $message")
            }

            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                requireActivity().toast("Payment cancelled")
                setPaymentBtn(false)
                Log.d(logTag, "payment cancelled : $message")
            }
        }
    }

    private fun getCoupin(paymentData: PaymentData){
        val addressId = checkoutViewModel.addressIdMLD.value ?: ""
        val isDeliverable = checkoutViewModel.isDeliverableMLD.value ?: false
        val merchantId = checkoutViewModel.merchantLD.value?.id ?: ""
        val expiryDate = checkoutViewModel.expiryDateMLD.value ?: ""

        val rewardIdQuantityMap = checkoutViewModel.rewardQuantityMLD.value
        val rewardsIdList = mutableListOf<String>()
        rewardIdQuantityMap?.let {
            it.forEach{
                for (item in 1..it.value){
                    rewardsIdList.add(it.key)
                }
            }
        }

        PreferenceMngr.setContext(requireContext())
        val token = PreferenceMngr.getToken() ?: ""

        val getCoupinRequestModel = GetCoupinRequestModel(false, rewardsIdList, addressId, isDeliverable, expiryDate, merchantId)

        Log.d(logTag, "rewardIdQuantity : $rewardsIdList")

        checkoutViewModel.getCoupin(getCoupinRequestModel, token).observe(viewLifecycleOwner, {
            it?.let {
                when(it.status){
                    Resource.Status.SUCCESS -> {
                        it.data?.let {
                            checkoutViewModel.coupinResponseModelMLD.value = it
                            payWithFlutterwave(paymentData, it)
                        }
                    }
                    Resource.Status.ERROR -> {
                        requireActivity().toast("Error initialising payment. Please try again later.")
                        setPaymentBtn(false)
                    }
                    Resource.Status.LOADING -> {
                        setPaymentBtn(true)
                    }
                }
            }
        })
    }

    private fun setPaymentBtn(isLoading : Boolean){
        if(isLoading){
            pay_btn.isEnabled = false
            pay_btn.text = getString(R.string.LOADING)
        }else{
            pay_btn.isEnabled = true
            val rewardsCost = checkoutViewModel.selectedCoupinsLD.value?.map { it.newPrice }?.sum() ?: 0F
            val deliveryCost = checkoutViewModel.deliveryPriceLD.value ?: 0
            val totalAmount = rewardsCost + deliveryCost
            pay_btn.text = "Pay ₦${setAmountFormat(totalAmount)}"
        }
    }

    private fun proceedToCoupinView(getCoupinResponseModel: GetCoupinResponseModel){
        val bookingId = getCoupinResponseModel.data?.booking?._id
        val shortCode = getCoupinResponseModel.data?.booking?.shortCode
        val merchant = checkoutViewModel.merchantLD.value
        val rewardObjectsString = checkoutViewModel.rewardObjectsString.value

        rewardObjectsString?.let {
            val rewardObjects = JSONArray(it)

            rewardObjects.let { rewardObjectsJSONArray ->

                val list = mutableListOf<JSONObject>()
                for(i in 0 until rewardObjectsJSONArray.length()){
                    val rewardObject = rewardObjects.getJSONObject(i)
                    list.add(rewardObject)
                }
                val jsonObjectList = arrayListOf<JSONObject>()
                list.forEach{
                    val jsonObject = JSONObject()
                    jsonObject.put("id", it)
                    jsonObjectList.add(jsonObject)
                }
                Log.d(logTag, "rewardDetailsString : $rewardObjectsString")

                merchant?.let {
                    val coupin = RewardListItem()
                    coupin.setBookingId(bookingId)
                    coupin.setBookingShortCode(shortCode)
                    coupin.setMerchantName(merchant.getTitle())
                    coupin.setMerchantAddress(merchant.getAddress())
                    coupin.setLatitude(merchant.getLatitude())
                    coupin.setLongitude(merchant.getLongitude())
                    coupin.setMerchantLogo(merchant.getLogo())
                    coupin.setMerchantBanner(merchant.getBanner())
                    coupin.isFavourited = merchant.isFavourite
                    coupin.setVisited(merchant.isVisited)
                    coupin.setStatus(getCoupinResponseModel.data?.booking?.status)
                    coupin.setRewardDetails(JSONArray(jsonObjectList).toString())
                    coupin.setRewardCount(list.size)

                    val intent = Intent(requireContext(), CoupinActivity::class.java)
                    intent.putExtra("coupin", coupin)
                    startActivity(intent)
                    requireActivity().finishAffinity()
                }
            }
        }
    }

    private fun reviewApplication(getCoupinResponseModel: GetCoupinResponseModel){
        val manager = ReviewManagerFactory.create(requireContext())
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener { _ ->
                    Log.d(logTag, "review success")
                    proceedToCoupinView(getCoupinResponseModel)
                }
            } else {
                Log.d(logTag, "review exception: ${task.exception}")
                proceedToCoupinView(getCoupinResponseModel)
            }
        }
    }
}