package com.kibou.abisoyeoke_lawal.coupinapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVPlacesSearchAdapter
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.PlacesSearchRecyclerClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.PlacesSearchRecyclerResource
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.AddAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_address.*
import org.jetbrains.anko.alert


@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener, PlacesSearchRecyclerClickListener {

    private lateinit var mMap: GoogleMap
    lateinit var RVPlacesSearchAdapter : RVPlacesSearchAdapter
    private val addAddressViewModel : AddAddressViewModel by viewModels()
    private val recyclerViewResource = arrayListOf<PlacesSearchRecyclerResource>()
    val logTag = "AddAddressActivity"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        location_back.setOnClickListener(this)
        setUpView()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.run{
            getMapAsync(this@AddAddressActivity)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@AddAddressActivity)
        }
        save_btn.setOnClickListener{ addAddress() }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        centerMapInUsersLocation()
        mMap.setOnMyLocationButtonClickListener {
            centerMapInUsersLocation()
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun centerMapInUsersLocation(){
        if(isLocationPermissionGranted()){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    positionMap(it)
                }
            }
        }else requestLocationPermission()
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.apply {
            isMyLocationButtonEnabled = true
            isZoomControlsEnabled = true
        }
    }

    private fun positionMap(location: Location){
        if(::mMap.isInitialized){
            val userLatLng = LatLng(location.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder().target(userLatLng).zoom(18F).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            val markerOptions = MarkerOptions().position(userLatLng).draggable(true)
            mMap.addMarker(markerOptions)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            location_back.id -> {
                onBackPressed()
            }
        }
    }

    private val textChangedListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                if(s.isNotEmpty()){
                    val searchString = s.toString().trim()
                    addAddressViewModel.searchPlaces(searchString).observe(this@AddAddressActivity, Observer {
                        when (it.status) {
                            Resource.Status.LOADING -> {
                            }
                            Resource.Status.ERROR -> {
                            }
                            Resource.Status.SUCCESS -> {
                                val recyclerViewResourceArray = arrayListOf<PlacesSearchRecyclerResource>()
                                if (it.data != null) {
                                    it.data.predictions.forEach {
                                        recyclerViewResourceArray.add(PlacesSearchRecyclerResource(it.structured_formatting.main_text, it.structured_formatting.secondary_text, it.place_id))
                                    }
                                    RVPlacesSearchAdapter.updateViewResource(recyclerViewResourceArray)
                                }
                            }
                        }
                    })
                }
            }
        }

    }

    private fun setUpView(){
        RVPlacesSearchAdapter = RVPlacesSearchAdapter(recyclerViewResource, this)
        val savePlacesLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        address_recycler.apply{
            adapter = RVPlacesSearchAdapter
            layoutManager = savePlacesLayoutManager
        }
        address_input.addTextChangedListener(textChangedListener)
    }


    override fun onPlacesSearchRecyclerClick(mainText: String?, secText: String?, placeId: String?) {
        try {
            var addressMain = mainText
            if(addressMain != null){
                addressMain = "$mainText, "
            }
            val address = ("${addressMain ?: ""}${secText ?: ""}").trim()
            address_input.setText(address)

            val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.LAT_LNG)
            placeId?.let {
                val request = FetchPlaceRequest.builder(placeId, fields).build()
                Places.createClient(applicationContext).fetchPlace(request)
                        .addOnSuccessListener {
                            it?.let {
                                Log.d(logTag, it.toString())
                                addAddressViewModel.setUserLatLng(it.place.latLng)
                            }
                        }
                        .addOnFailureListener {
                            if (it is ApiException) {
                                it.printStackTrace()
                            }
                        }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun addAddress(){
        val address = address_input.text.toString().trim()
        val phoneNumber = phone_number_input.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val userLat = addAddressViewModel.userLatLngLD.value?.latitude
        val userLong = addAddressViewModel.userLatLngLD.value?.longitude

        if(userLat == null || userLong == null){
            Toast.makeText(this, "Invalid location", Toast.LENGTH_SHORT).show()
            return
        }
        val token = PreferenceMngr.getToken() ?: ""
        val addressModel = AddressModel(address, userLong, userLat, phoneNumber, token)

        addAddressViewModel.addUserAddress(addressModel).observe(this, {
            it?.let {
                when(it.status){
                    Resource.Status.ERROR ->{
                        save_btn.isEnabled = true
                        save_btn.text = getString(R.string.bt_save)
                        alert( "Error adding address. Please try again later.").show()
                    }
                    Resource.Status.SUCCESS ->{
                        save_btn.isEnabled = true
                        save_btn.text = getString(R.string.bt_save)
                        alert( "Address added successfully.").show()
                        address_input.setText("")
                        phone_number_input.setText("")
                    }
                    Resource.Status.LOADING ->{
                        save_btn.isEnabled = false
                        save_btn.text = getString(R.string.LOADING)
                    }
                }
            }
        })

    }

    private fun requestLocationPermission(){
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                centerMapInUsersLocation()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
            Toast.makeText(this, "Location permission is needed to fill in your address.", Toast.LENGTH_LONG).show()
        }else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    centerMapInUsersLocation()
                } else {
                    Toast.makeText(this, "Unable to display your location on the map because location permission is denied.", Toast.LENGTH_LONG).show()
                }
            }

    private fun isLocationPermissionGranted() : Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}