package com.kibou.abisoyeoke_lawal.coupinapp.utils

import android.content.Intent.getIntent
import java.math.RoundingMode
import java.text.DecimalFormat


fun setAmountFormat(amount : Number) : String {
    val df = DecimalFormat("#0.00")
    df.roundingMode = RoundingMode.HALF_EVEN
    return df.format(amount)
}