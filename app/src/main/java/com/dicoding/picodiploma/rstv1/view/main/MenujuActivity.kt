package com.dicoding.picodiploma.rstv1.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.rstv1.R
import com.dicoding.picodiploma.rstv1.view.plan.LocationDescriptionFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MenujuActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewModel: JourneyViewModel
    private lateinit var mMap: GoogleMap

    private val startLocation = LatLng(-6.974296096529034, 108.48384394059156)
    private val endLocation = LatLng(-7.192361151752414, 107.2346728532833)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menuju)

        viewModel = ViewModelProvider(this).get(JourneyViewModel::class.java)

        val destination = intent.getStringExtra("DESTINATION") ?: return

        // Set destination name
        findViewById<TextView>(R.id.textViewName).text = destination

        // Inisialisasi peta
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.buttonStart).setOnClickListener {
            startJourneyWithGoogleMaps()
        }

        // Tambahkan listener untuk tombol Show Description
        findViewById<Button>(R.id.buttonShowDescription).setOnClickListener {
            showLocationDescriptionFragment()
        }

        // Set up FAB listeners
        findViewById<FloatingActionButton>(R.id.fab_location).setOnClickListener {
            // Handle location FAB click
            centerMapOnDestination()
        }

    }

    private fun startJourneyWithGoogleMaps() {
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${startLocation.latitude},${startLocation.longitude}&destination=${endLocation.latitude},${endLocation.longitude}&travelmode=driving")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Jika Google Maps tidak terinstall, buka di browser
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(browserIntent)
        }
    }

    private fun showLocationDescriptionFragment() {
        val fragment = LocationDescriptionFragment()
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Tambahkan marker untuk lokasi awal dan tujuan
        mMap.addMarker(MarkerOptions().position(startLocation).title("Lokasi Awal"))
        mMap.addMarker(MarkerOptions().position(endLocation).title("Lokasi Tujuan"))

        // Atur zoom level dan posisi kamera untuk menampilkan kedua lokasi
        val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
            .include(startLocation)
            .include(endLocation)
            .build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun centerMapOnDestination() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endLocation, 15f))
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}