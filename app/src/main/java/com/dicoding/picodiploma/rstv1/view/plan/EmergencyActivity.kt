package com.dicoding.picodiploma.rstv1.view.plan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.rstv1.R
import com.dicoding.picodiploma.rstv1.view.main.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmergencyActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up click listeners for fab_emergency and ic_close
        findViewById<FloatingActionButton>(R.id.fab_emergency).setOnClickListener {
            // Refresh the map or perform any other action
            onMapReady(mMap)
        }

        findViewById<ImageView>(R.id.ic_close).setOnClickListener {
            // Return to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Clear previous markers and polylines
        mMap.clear()

        // Simulasi lokasi pengguna
        val userLocation = LatLng(-6.2088, 106.8456) // Jakarta

        // Simulasi lokasi SPBU terdekat
        val spbuLocation = LatLng(-6.2100, 106.8470) // Lokasi SPBU simulasi

        // Tambahkan marker untuk lokasi pengguna
        mMap.addMarker(MarkerOptions().position(userLocation).title("Lokasi Anda"))

        // Tambahkan marker untuk lokasi SPBU
        mMap.addMarker(MarkerOptions().position(spbuLocation).title("SPBU Terdekat"))

        // Buat polyline untuk rute fake
        val polylineOptions = PolylineOptions()
            .add(userLocation)
            .add(LatLng(-6.2095, 106.8460)) // Titik tengah untuk membuat rute terlihat lebih realistis
            .add(spbuLocation)
            .color(resources.getColor(R.color.colorPrimary))
            .width(5f)

        mMap.addPolyline(polylineOptions)

        // Pindahkan kamera ke lokasi pengguna
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

        // Update UI elements with route information
        updateRouteInfo()
    }

    private fun updateRouteInfo() {
        // Update UI elements with simulated route information
        findViewById<android.widget.TextView>(R.id.distance_km).text = "2.5 km"
        findViewById<android.widget.TextView>(R.id.destination_text).text = "ke arah SPBU Terdekat"
        findViewById<android.widget.TextView>(R.id.travel_time_tv).text = "10 minutes"
        findViewById<android.widget.TextView>(R.id.distance_tv).text = "2.5 km"
        findViewById<android.widget.TextView>(R.id.arrival_time_tv).text = "09:45"
    }
}