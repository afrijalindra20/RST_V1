package com.dicoding.picodiploma.rstv1.view.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.picodiploma.rstv1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Set up map settings
    }

    fun updateMap(route: List<LatLng>, places: List<Place>) {
        if (!::map.isInitialized) return

        map.clear()

        // Draw route
        val polylineOptions = PolylineOptions().addAll(route).color(Color.BLUE)
        map.addPolyline(polylineOptions)

        // Add markers for places
        for (place in places) {
            val markerOptions = MarkerOptions()
                .position(place.location)
                .title(place.name)
                .snippet("${place.type} - Distance: ${place.distance} km")
            map.addMarker(markerOptions)
        }

        // Move camera to show the entire route
        val builder = LatLngBounds.Builder()
        for (point in route) {
            builder.include(point)
        }
        val bounds = builder.build()
        val padding = 100 // offset from edges of the map in pixels
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        map.animateCamera(cameraUpdate)
    }

    fun updateUserLocation(location: LatLng) {
        if (!::map.isInitialized) return

        map.clear()
        map.addMarker(MarkerOptions().position(location).title("Your Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}