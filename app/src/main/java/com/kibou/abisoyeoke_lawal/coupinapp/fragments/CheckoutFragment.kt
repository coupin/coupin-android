package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.content.Intent
import android.os.Bundle
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
import com.google.gson.Gson
import com.kibou.abisoyeoke_lawal.coupinapp.BuildConfig
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.activities.CoupinActivity
import com.kibou.abisoyeoke_lawal.coupinapp.activities.HomeActivity
import com.kibou.abisoyeoke_lawal.coupinapp.models.*
import com.kibou.abisoyeoke_lawal.coupinapp.utils.*
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.GetCoupinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_checkout.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

@AndroidEntryPoint
class CheckoutFragment : Fragment(), View.OnClickListener {

    private val checkoutViewModel : GetCoupinViewModel by activityViewModels()
    private var createdCoupin = false
    private lateinit var savedResponse : GetCoupinResponseModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpOnClickListeners()
        fillInUserDetails()

    }
    
    private fun fillInUserDetails(){
        PreferenceManager.setContext(requireContext())
        val userString = PreferenceManager.getCurrentUser()
        userString?.let{
            val userEmail = it.email
            val temp: String = it.name
            val names = temp.split(" ".toRegex()).toTypedArray()
            email_input.setText(userEmail)
            first_name_input.setText(names[0])
            last_name_input.setText(names[1])
        }

        pay_btn.text = "Pay ₦${setAmountFormat(getTotalAmount())}"
    }

    private fun setUpOnClickListeners(){
        pay_btn.setOnClickListener(this)
        checkout_back.setOnClickListener(this)
    }

    private fun getTotalAmount(): Double {
        val rewards = checkoutViewModel.selectedCoupinsLD.value
        val rewardsPrice = rewards?.map {
            val quantity = it.selectedQuantity
            it.price.newPrice * quantity
        }?.sum() ?: 0F

        val deliveryPrice = checkoutViewModel.deliveryPriceLD.value ?: 0
        return (rewardsPrice + deliveryPrice).toDouble()
    }

    private fun payWithFlutterwave(paymentData: PaymentData, getCoupinResponseModel: GetCoupinResponseModel){

        val reference = getCoupinResponseModel.data?.reference ?: ""
        val coupinId = getCoupinResponseModel.data?.booking?._id ?: ""

        RaveUiManager(this)
            .setAmount(getTotalAmount())
            .setCurrency(currency)
            .setEmail(paymentData.email)
            .setfName(paymentData.firstName)
            .setlName(paymentData.lastName)
            .setPublicKey(BuildConfig.COUPIN_FLUTTERWAVE_PUBLIC_KEY)
            .setEncryptionKey(BuildConfig.COUPIN_ENCRYPTION_KEY)
            .setTxRef(reference)
            .showStagingLabel(false)
            .acceptAccountPayments(true)
            .acceptCardPayments(true)
            .allowSaveCardFeature(true)
            .shouldDisplayFee(true)
            .onStagingEnv(BuildConfig.DEBUG)
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

                val user = PreferenceManager.getCurrentUser()
                PreferenceManager.addToTotalCoupinsGenerated(user.id)
                checkoutViewModel.tempBlackListMLD.value?.let {
                    PreferenceManager.setBlacklist(it)
                }

                checkoutViewModel.coupinResponseModelMLD.value?.let {
                    proceedToCoupinView(it)
                }
            }

            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                requireActivity().toast("Payment error")
                setPaymentBtn(false)
            }

            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                requireActivity().toast("Payment cancelled")
                setPaymentBtn(false)
            }
        }
    }

    private fun getCoupin(paymentData: PaymentData){
        if (!createdCoupin) {
            val addressId = checkoutViewModel.addressIdMLD.value ?: ""
            val isDeliverable = checkoutViewModel.isDeliverableMLD.value ?: false
            val merchantId = checkoutViewModel.merchantLD.value?.id ?: ""
            val expiryDate = checkoutViewModel.expiryDateMLD.value ?: ""
            val coupinId = checkoutViewModel.coupinIdMLD.value ?: ""

            val rewards = checkoutViewModel.selectedCoupinsLD.value
            val rewardsIdList = mutableListOf<String>()

            rewards?.let {
                for (item in it) {
                    for (i in 1..item.selectedQuantity) {
                        rewardsIdList.add(item.id)
                    }
                }
            }

            PreferenceManager.setContext(requireContext())
            val token = PreferenceManager.getToken() ?: ""

            val getCoupinRequestModel =
                GetCoupinRequestModel(false, rewardsIdList, addressId, isDeliverable, expiryDate, merchantId, coupinId)

            checkoutViewModel.getCoupin(getCoupinRequestModel, token).observe(viewLifecycleOwner, {
                it?.let {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            it.data?.let {
                                checkoutViewModel.coupinResponseModelMLD.value = it
                                savedResponse = it
                                payWithFlutterwave(paymentData, it)
                                createdCoupin = true
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
        } else {
            payWithFlutterwave(paymentData, savedResponse);
        }
    }

    private fun setPaymentBtn(isLoading : Boolean){
        if(isLoading){
            pay_btn.isEnabled = false
            pay_btn.text = getString(R.string.LOADING)
        }else{
            pay_btn.isEnabled = true
            pay_btn.text = "Pay ₦${setAmountFormat(getTotalAmount())}"
        }
    }

    private fun proceedToCoupinView(getCoupinResponseModel: GetCoupinResponseModel){
        val bookingId = getCoupinResponseModel.data?.booking?._id
        val shortCode = getCoupinResponseModel.data?.booking?.shortCode
        val merchant = checkoutViewModel.merchantLD.value
        val rewardCount = getCoupinResponseModel.data?.booking?.rewardId?.size ?: 0
        val rewards = Gson().toJson(getCoupinResponseModel.data?.booking?.rewardId)

        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.putExtra("fromCoupin", true)
        startActivity(intent)
        requireActivity().finishAffinity()

        // TODO: Uncomment once you convert to retrofit
        merchant?.let {
            val coupin = RewardsListItemV2()
            coupin.id = bookingId
            coupin.shortCode = shortCode
            coupin.merchant = InnerItem()
            coupin.merchant.merchantInfo = InnerItem.MerchantInfo()
            coupin.merchant.merchantInfo.companyName = merchant.name
            coupin.merchant.merchantInfo.address = merchant.address
            coupin.merchant.merchantInfo.location = doubleArrayOf(merchant.location.longitude, merchant.location.latitude)
            coupin.merchant.merchantInfo.logo = Image()
            coupin.merchant.merchantInfo.logo.url = merchant.logo.url
            coupin.merchant.merchantInfo.banner = Image()
            coupin.merchant.merchantInfo.banner.url = merchant.banner.url
            coupin.favourite = merchant.favourite
            coupin.visited = merchant.visited
            coupin.status = getCoupinResponseModel.data?.booking?.status
            coupin.rewardsArray = checkoutViewModel.selectedCoupinsLD.value
            coupin.rewardCount = rewardCount

            val intent = Intent(requireContext(), CoupinActivity::class.java)
            intent.putExtra("coupin", coupin)
            intent.putExtra("fromPurchase", true)
            startActivity(intent)
            requireActivity().finishAffinity()
        }
    }
}