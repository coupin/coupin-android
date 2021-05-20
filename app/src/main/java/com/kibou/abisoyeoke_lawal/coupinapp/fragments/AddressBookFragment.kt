package com.kibou.abisoyeoke_lawal.coupinapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kibou.abisoyeoke_lawal.coupinapp.R
import kotlinx.android.synthetic.main.fragment_address_book.*

class AddressBookFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_address_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        add_address.setOnClickListener(this)
        address_book_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            add_address.id -> {
                findNavController().navigate(R.id.locationFragment)
            }
            address_book_back.id -> {
                requireActivity().onBackPressed()
            }
        }
    }
}