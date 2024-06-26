package com.dicoding.picodiploma.rstv1.view.plan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
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
    private val userLocation = LatLng(-6.974287473230536, 108.47927146667585) // kosan
    private val spbuLocation = LatLng(-6.974981181171036, 108.48448465905079) // Lokasi SPBU kuningan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<FloatingActionButton>(R.id.fab_emergency).setOnClickListener {
            onMapReady(mMap)
        }

        findViewById<ImageView>(R.id.ic_close).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<FloatingActionButton>(R.id.fab_location).setOnClickListener {
            focusOnUserLocation()
        }

        findViewById<Button>(R.id.btn_start_journey).setOnClickListener {
            openGoogleMapsWithRoute()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showRouteWithMarkers()
    }

    private fun showRouteWithMarkers() {
        mMap.clear()

        mMap.addMarker(MarkerOptions().position(userLocation).title("Lokasi Anda"))
        mMap.addMarker(MarkerOptions().position(spbuLocation).title("SPBU Terdekat"))

        val polylineOptions = PolylineOptions()
            .add(userLocation)
            .add(LatLng(-6.973987882624612, 108.47943874648924))
            .add(LatLng(-6.974203080701184, 108.4797789629548))
            .add(LatLng(-6.973523668192516, 108.48021811523832))
            .add(LatLng(-6.975349190436416, 108.48413568742497))
            .add(LatLng(-6.975015160470556, 108.48435333565176))
            .add(LatLng(-6.975083050606679, 108.48447336450188))
            .add(spbuLocation)
            .color(resources.getColor(R.color.colorPrimary))
            .width(5f)

        mMap.addPolyline(polylineOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
        updateRouteInfo()
    }

    private fun focusOnUserLocation() {
        // Zoom ke lokasi pengguna dengan level zoom yang lebih dekat
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
    }

    private fun openGoogleMapsWithRoute() {
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${userLocation.latitude},${userLocation.longitude}&destination=${spbuLocation.latitude},${spbuLocation.longitude}&travelmode=driving")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    private fun updateRouteInfo() {
        findViewById<android.widget.TextView>(R.id.distance_km).text = "800 m"
        findViewById<android.widget.TextView>(R.id.destination_text).text = "ke arah SPBU Terdekat"
        findViewById<android.widget.TextView>(R.id.travel_time_tv).text = "2 minutes"
        findViewById<android.widget.TextView>(R.id.distance_tv).text = "800 m"
        findViewById<android.widget.TextView>(R.id.arrival_time_tv).text = "15:30"
    }
}