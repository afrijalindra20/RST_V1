package com.dicoding.picodiploma.rstv1.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.rstv1.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.android.PolyUtil
import com.google.maps.model.PlaceType
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import com.google.android.gms.common.api.Status

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoApiContext: GeoApiContext
    private var currentLocation: LatLng? = null
    private var destinationLatLng: LatLng? = null
    private var navigationFragment: NavigationFragment? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        Places.initialize(applicationContext, "AIzaSyDjrJiAFsGNqkgEugEEuA7f8f_GQI6PZww")
        placesClient = Places.createClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        geoApiContext = GeoApiContext.Builder()
            .apiKey("AIzaSyDjrJiAFsGNqkgEugEEuA7f8f_GQI6PZww")
            .build()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupPlacesAutocomplete()

        findViewById<Button>(R.id.navigate_button).setOnClickListener {
            startNavigation()
        }

        findViewById<Button>(R.id.geocode_button).setOnClickListener {
            showGeocodeDialog()
        }

        findViewById<Button>(R.id.nearby_search_button).setOnClickListener {
            showNearbySearchDialog()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun setupPlacesAutocomplete() {
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    destinationLatLng = latLng
                    mMap.clear()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    addMarkerAndShowInfo(place)
                    showNavigationOption()
                }
            }

            override fun onError(status: Status) {
                Log.e("PlacesAPI", "An error occurred: $status")
            }
        })
    }

    private fun showNavigationOption() {
        val navigateButton = findViewById<Button>(R.id.navigate_button)
        navigateButton.visibility = View.VISIBLE
        navigateButton.setOnClickListener {
            startNavigation()
        }
    }

    private fun startNavigation() {
        currentLocation?.let { origin ->
            destinationLatLng?.let { destination ->
                navigationFragment = NavigationFragment.newInstance(origin, destination)
                navigationFragment?.show(supportFragmentManager, "NavigationFragment")
            } ?: run {
                Toast.makeText(this, "Please select a destination first", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showGeocodeDialog() {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.hint = "Enter address"
        builder.setView(input)
        builder.setPositiveButton("Search") { _, _ ->
            val address = input.text.toString()
            geocodeAddress(address)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun geocodeAddress(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocationName(address, 1)
            addresses?.firstOrNull()?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                destinationLatLng = latLng
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latLng).title(address))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                showNavigationOption()
            } ?: run {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error: ${e.message}")
            Toast.makeText(this, "An error occurred during geocoding", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f))
            }
        }
    }

    private fun addMarkerAndShowInfo(place: Place) {
        mMap.clear()
        val marker = mMap.addMarker(MarkerOptions().position(place.latLng!!).title(place.name))
        marker?.showInfoWindow()

        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER)
        val request = FetchPlaceRequest.newInstance(place.id!!, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val placeDetails = response.place
            showPlaceDetails(placeDetails)
        }.addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Place details not found: ${exception.message}")
        }
    }

    private fun showPlaceDetails(place: Place) {
        PlaceDetailsBottomSheet.newInstance(place).show(supportFragmentManager, "PlaceDetailsBottomSheet")
    }

    private fun showNearbySearchDialog() {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.hint = "Enter place type (e.g., restaurant, park)"
        builder.setView(input)
        builder.setPositiveButton("Search") { _, _ ->
            val placeType = input.text.toString()
            currentLocation?.let { location ->
                searchNearbyPlaces(location, placeType)
            } ?: run {
                Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun searchNearbyPlaces(location: LatLng, type: String) {
        val request = PlacesApi.nearbySearchQuery(geoApiContext, com.google.maps.model.LatLng(location.latitude, location.longitude))
            .radius(1000) // Search radius in meters
            .type(PlaceType.valueOf(type.uppercase()))

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = request.await()
                withContext(Dispatchers.Main) {
                    for (place in result.results) {
                        mMap.addMarker(MarkerOptions()
                            .position(LatLng(place.geometry.location.lat, place.geometry.location.lng))
                            .title(place.name))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}