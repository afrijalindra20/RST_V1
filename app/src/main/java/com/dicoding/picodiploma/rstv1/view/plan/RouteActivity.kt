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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this, MulaiActivity::class.java))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Lokasi palsu untuk demonstrasi
        val currentLocation = LatLng(-6.2088, 106.8456) // Jakarta
        val curugCitambur = LatLng(-7.3258, 107.3877) // Curug Citambur

        // Tambahkan marker
        mMap.addMarker(MarkerOptions().position(currentLocation).title("Lokasi Anda"))
        mMap.addMarker(MarkerOptions().position(curugCitambur).title("Curug Citambur"))

        // Gambar garis rute palsu
        val routePoints = listOf(
            currentLocation,
            LatLng(-6.5971, 106.8060), // Titik tengah palsu
            curugCitambur
        )

        mMap.addPolyline(
            PolylineOptions()
                .addAll(routePoints)
                .width(5f)
                .color(Color.BLUE)
        )

        // Pindahkan kamera ke posisi yang mencakup kedua lokasi
        val bounds = LatLngBounds.Builder().include(currentLocation).include(curugCitambur).build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }
}