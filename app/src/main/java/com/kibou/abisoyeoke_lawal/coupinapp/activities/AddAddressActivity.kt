package com.kibou.abisoyeoke_lawal.coupinapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVPlacesSearchAdapter
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.PlacesSearchRecyclerClickListener
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressModel
import com.kibou.abisoyeoke_lawal.coupinapp.models.AddressSetTextFrom
import com.kibou.abisoyeoke_lawal.coupinapp.models.PlacesSearchRecyclerResource
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager
import com.kibou.abisoyeoke_lawal.coupinapp.utils.Resource
import com.kibou.abisoyeoke_lawal.coupinapp.view_models.AddAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener, PlacesSearchRecyclerClickListener {

    private lateinit var mMap: GoogleMap
    lateinit var RVPlacesSearchAdapter : RVPlacesSearchAdapter
    private val addAddressViewModel : AddAddressViewModel by viewModels()
    private val recyclerViewResource = arrayListOf<PlacesSearchRecyclerResource>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var markerOptions : MarkerOptions
    private lateinit var addressMarker : Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.run{
            getMapAsync(this@AddAddressActivity)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@AddAddressActivity)
        }
        setUpOnClickListeners()
        setUpView()
    }

    @SuppressLint("MissingPermission")
    private fun setUpOnClickListeners(){
        save_btn.setOnClickListener(this)
        location_back.setOnClickListener(this)

        if(isLocationPermissionGranted()){
            if(::mMap.isInitialized){
                mMap.setOnMyLocationButtonClickListener {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        location?.let {
                            positionMap(it.latitude, it.longitude)
                        }
                    }
                    true
                }
            }
        }else requestLocationPermission()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        centerMapInUsersLocation()
        mMap.uiSettings.apply {
            isMyLocationButtonEnabled = true
        }
        if(isLocationPermissionGranted()){
            mMap.isMyLocationEnabled = true
        }else requestLocationPermission()

        setMapDragListener()
    }

    private fun setMapDragListener(){
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(p0: Marker) {}
            override fun onMarkerDrag(p0: Marker) {}

            override fun onMarkerDragEnd(marker: Marker) {
                val latLng: LatLng = marker.position
                positionMap(latLng.latitude, latLng.longitude)
                markerOptions.position(marker.position)
                val geocoder = Geocoder(this@AddAddressActivity, Locale.getDefault())
                try {
                    val df = DecimalFormat("#.0000")
                    df.roundingMode = RoundingMode.HALF_EVEN

                    val formattedLatitude = df.format(latLng.latitude).toDouble()
                    val formattedLongitude = df.format(latLng.longitude).toDouble()
                    GlobalScope.launch {
                        val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)[0]
                        runOnUiThread {
                            addAddressViewModel.mldAddressSetTextFrom.value = AddressSetTextFrom.MAP_PIN
                            address_input.setText(address.getAddressLine(0))
                            addAddressViewModel.setUserLatLng(LatLng(formattedLatitude, formattedLongitude)) // set formatted lat lng to api
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun centerMapInUsersLocation(){
        if(isLocationPermissionGranted()){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    positionMap(it.latitude, it.longitude)
                    markerOptions = MarkerOptions().position(LatLng(it.latitude, it.longitude)).draggable(true)
                    addressMarker = mMap.addMarker(markerOptions)
                }
            }
        }else requestLocationPermission()
    }

    private fun positionMap(latitude: Double, longitude: Double){
        if(::mMap.isInitialized){
            val userLatLng = LatLng(latitude, longitude)
            val cameraPosition = CameraPosition.Builder().target(userLatLng).zoom(18F).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            location_back.id -> onBackPressed()
            save_btn.id -> prepareToAddAddress()
        }
    }

    private fun preparePlaceSearch(searchString: String){
        val addressInputFrom = addAddressViewModel.mldAddressSetTextFrom.value
        addressInputFrom?.let {
            if(it == AddressSetTextFrom.PLACE_SEARCH_RESULT){
                performPlaceSearch(searchString)
            }else if( it == AddressSetTextFrom.MAP_PIN) {
                address_recycler.visibility = View.GONE
            }else {
                performPlaceSearch(searchString)
            }
        }
    }

    private fun performPlaceSearch(searchString : String){
        address_recycler.visibility = View.VISIBLE
        addAddressViewModel.searchPlaces(searchString).observe(this@AddAddressActivity, {
            when (it.status) {
                Resource.Status.LOADING -> {
                    progress_bar.visibility = View.VISIBLE
                }
                Resource.Status.ERROR -> {
                    progress_bar.visibility = View.GONE
                    Toast.makeText(this@AddAddressActivity, "An error occured. Please try againn later.", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.SUCCESS -> {
                    progress_bar.visibility = View.GONE
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

    private val textChangedListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                addAddressViewModel.mldAddressSetTextFrom.value = AddressSetTextFrom.PLACE_SEARCH_RESULT
                val searchString = s.toString().trim()
                preparePlaceSearch(searchString)
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
            addAddressViewModel.mldAddressSetTextFrom.value = AddressSetTextFrom.PLACE_SEARCH_RESULT
            address_input.setText(address)

            val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.LAT_LNG)
            placeId?.let {
                val request = FetchPlaceRequest.builder(placeId, fields).build()
                Places.createClient(applicationContext).fetchPlace(request)
                        .addOnSuccessListener {
                            it?.let {
                                addAddressViewModel.setUserLatLng(it.place.latLng)
                                it.place.latLng?.let {
                                    positionMap(it.latitude, it.longitude)
                                    if(::addressMarker.isInitialized && ::markerOptions.isInitialized){
                                        addressMarker.position = it
                                        markerOptions.position(it)
                                    }
                                }
                                preparePlaceSearch("")  // to clear recycler view after click
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

    private fun prepareToAddAddress(){
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
        val token = PreferenceManager.getToken() ?: ""
        val addressModel = AddressModel(address, userLong, userLat, phoneNumber, token)
        addAddressToNetwork(addressModel)
    }

    private fun addAddressToNetwork(addressModel: AddressModel){
        addAddressViewModel.addAddressToNetwork(addressModel).observe(this, {
            it?.let {
                when (it.status) {
                    Resource.Status.ERROR -> {
                        save_btn.isEnabled = true
                        save_btn.text = getString(R.string.bt_save)
                        Toast.makeText(this@AddAddressActivity, "Error adding address. Please try again later.",  Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.SUCCESS -> {
                        save_btn.isEnabled = true
                        save_btn.text = getString(R.string.bt_save)
                        it.data?.let {
                            Toast.makeText(this@AddAddressActivity,"Address added successfully.",  Toast.LENGTH_SHORT).show()
                            it.address?.let {
                                addAddressViewModel.addAddressToDB(it)
                            }
                            finish()
                        }
                    }
                    Resource.Status.LOADING -> {
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