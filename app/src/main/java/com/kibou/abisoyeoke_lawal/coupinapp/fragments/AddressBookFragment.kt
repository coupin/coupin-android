package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kibou.abisoyeoke_lawal.coupinapp.R

class AddressBookFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_address_book, container, false)
    }
}