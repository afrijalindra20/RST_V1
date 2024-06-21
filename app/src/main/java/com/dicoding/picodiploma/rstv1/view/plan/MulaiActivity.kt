package com.dicoding.picodiploma.rstv1.view.plan

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.dicoding.picodiploma.rstv1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil

class MulaiActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var startLocation: LatLng
    private lateinit var endLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mulai)

        val startLat = intent.getDoubleExtra("START_LAT", 0.0)
        val startLng = intent.getDoubleExtra("START_LNG", 0.0)
        val endLat = intent.getDoubleExtra("END_LAT", 0.0)
        val endLng = intent.getDoubleExtra("END_LNG", 0.0)

        startLocation = LatLng(startLat, startLng)
        endLocation = LatLng(endLat, endLng)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupMap(googleMap)
    }

    private fun setupMap(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.addMarker(MarkerOptions().position(startLocation).title("Lokasi Anda"))

        // Menambahkan marker khusus untuk tujuan akhir
        val endMarker = mMap.addMarker(MarkerOptions()
            .position(endLocation)
            .title("Curug Citambur")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        // Membuat rute dengan panah
        val routePoints = getRoutePoints(startLocation, endLocation)
        val polylineOptions = PolylineOptions()
            .addAll(routePoints)
            .width(5f)
            .color(Color.BLUE)
            .geodesic(true)

        mMap.addPolyline(polylineOptions)

        // Menambahkan panah pada rute
        for (i in 0 until routePoints.size - 1) {
            val startPoint = routePoints[i]
            val endPoint = routePoints[i + 1]
            val heading = SphericalUtil.computeHeading(startPoint, endPoint)
            mMap.addPolyline(PolylineOptions()
                .add(startPoint, endPoint)
                .width(5f)
                .color(Color.BLUE)
                .geodesic(true)
                .startCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow), 10f)))
        }

        val bounds = LatLngBounds.Builder().include(startLocation).include(endLocation).build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun getRoutePoints(start: LatLng, end: LatLng): List<LatLng> {
        // Ini adalah contoh sederhana. Dalam aplikasi nyata, Anda mungkin ingin menggunakan
        // layanan routing seperti Google Directions API untuk mendapatkan rute yang sebenarnya.
        return listOf(
            start,
            LatLng((start.latitude + end.latitude) / 2, (start.longitude + end.longitude) / 2),
            end
        )
    }
}