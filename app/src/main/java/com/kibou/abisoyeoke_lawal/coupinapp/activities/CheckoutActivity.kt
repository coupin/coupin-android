package com.kibou.abisoyeoke_lawal.coupinapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.kibou.abisoyeoke_lawal.coupinapp.R
import kotlinx.android.synthetic.main.activity_checkout.*
import org.jetbrains.anko.toast


class CheckoutActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setUpOnClickListeners()
    }

    private fun setUpOnClickListeners(){
        pay_btn.setOnClickListener(this)
        checkout_back.setOnClickListener(this)
    }

    private fun payWithFlutterwave(paymentData : PaymentData){
        RaveUiManager(this)
            .setAmount(2.00)
            .setCurrency("NGN")
            .setEmail(paymentData.email)
            .setfName(paymentData.firstName)
            .setlName(paymentData.lastName)
            .setPublicKey("FLWPUBK_TEST-a77065e7124ac47e3cb2a7852ac9a615-X")
            .setEncryptionKey("FLWSECK_TESTda74354150c9")
            .setTxRef("sksksk")
            .acceptAccountPayments(true)
            .acceptCardPayments(true)
            .allowSaveCardFeature(true)
            .shouldDisplayFee(true)
            .initialize();
    }

    override fun onClick(v: View?) {
        when(v?.id){
            pay_btn.id -> {
                prepareForPayment()
            }
            checkout_back.id -> {
                onBackPressed()
            }
        }
    }

    private fun prepareForPayment(){
        val email = email_input.text.toString().trim()
        val firstName = first_name_input.text.toString().trim()
        val lastName = last_name_input.text.toString().trim()

        if(!isEmailValid(email)){
            toast("Enter a valid email address")
            return
        }

        if(firstName.isEmpty()){
            toast("Enter a valid first name.")
            return
        }

        if(lastName.isEmpty()){
            toast("Enter a valid last name.")
            return
        }
        val paymentData = PaymentData(email, firstName, lastName)
        payWithFlutterwave(paymentData)
    }

    fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    data class PaymentData(val email : String, val firstName : String, val lastName : String)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE) {
            val message = data?.getStringExtra("response")
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS $message", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR $message", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED $message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}