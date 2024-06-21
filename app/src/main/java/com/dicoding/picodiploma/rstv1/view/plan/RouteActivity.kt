package com.dicoding.picodiploma.rstv1.view.plan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.rstv1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class RouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var startLocation: LatLng
    private lateinit var endLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        val startLat = intent.getDoubleExtra("START_LAT", 0.0)
        val startLng = intent.getDoubleExtra("START_LNG", 0.0)
        val endLat = intent.getDoubleExtra("END_LAT", 0.0)
        val endLng = intent.getDoubleExtra("END_LNG", 0.0)

        startLocation = LatLng(startLat, startLng)
        endLocation = LatLng(endLat, endLng)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this, MulaiActivity::class.java))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupMap(googleMap)
    }

    private fun setupMap(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.addMarker(MarkerOptions().position(startLocation).title("Lokasi Anda"))
        mMap.addMarker(MarkerOptions().position(endLocation).title("Curug Citambur"))

        val routePoints = listOf(
            startLocation,
            LatLng((startLocation.latitude + endLocation.latitude) / 2,
                (startLocation.longitude + endLocation.longitude) / 2), // Titik tengah
            endLocation
        )

        mMap.addPolyline(
            PolylineOptions()
                .addAll(routePoints)
                .width(5f)
                .color(Color.BLUE)
        )

        val bounds = LatLngBounds.Builder().include(startLocation).include(endLocation).build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }
}