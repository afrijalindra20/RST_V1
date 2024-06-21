package com.dicoding.picodiploma.rstv1.view.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.picodiploma.rstv1.R
import com.dicoding.picodiploma.rstv1.view.plan.EmergencyActivity
import com.dicoding.picodiploma.rstv1.view.plan.ProfileFragment
import com.dicoding.picodiploma.rstv1.view.plan.StartJourneyListener
import com.dicoding.picodiploma.rstv1.view.plan.VehicleInputFragment
import com.dicoding.picodiploma.rstv1.view.welcome.WelcomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), OnMapReadyCallback, StartJourneyListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var userMarker: Marker? = null

    private lateinit var fragmentContainer: View
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var searchBarContainer: View
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var fabLocation: FloatingActionButton
    private lateinit var fabEmergency: FloatingActionButton
    private lateinit var bottomNavigation: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate called")

        // Initialize views
        fragmentContainer = findViewById(R.id.fragment_container)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        searchBarContainer = findViewById(R.id.search_bar_container)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        fabLocation = findViewById(R.id.fab_location)
        fabEmergency = findViewById(R.id.fab_emergency)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Set up AutoCompleteTextView
        setupAutoCompleteTextView()

        // Initialize map
        mapFragment.getMapAsync(this)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up bottom navigation buttons
        findViewById<ImageButton>(R.id.btn_home).setOnClickListener {
            showLogoutDialog()
        }

        findViewById<ImageButton>(R.id.btn_add).setOnClickListener {
            loadFragment(VehicleInputFragment())
        }

        findViewById<ImageButton>(R.id.btn_profile).setOnClickListener {
            loadFragment(ProfileFragment())
        }

        // Set up FABs
        fabEmergency.setOnClickListener {
            showEmergencyDialog()
        }

        fabLocation.setOnClickListener {
            checkLocationPermissionAndShowUserLocation()
        }
    }

    private fun showEmergencyDialog() {
        AlertDialog.Builder(this)
            .setTitle("Emergency")
            .setMessage("Ingin mencari SPBU terdekat?")
            .setPositiveButton("Ya") { _, _ ->
                val intent = Intent(this, EmergencyActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun setupAutoCompleteTextView() {
        val suggestions = listOf(
            "Curug Citambur, Cianjur, Jawa Barat",
            "Curug Cilember, Megamendung, Jawa Barat",
            "Curug Leuwi Hejo, Sentul, Kabupaten Cirebon"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedDestination = suggestions[position]
            navigateToMenujuActivity(selectedDestination)
        }
    }

    private fun navigateToMenujuActivity(destination: String) {
        val intent = Intent(this, MenujuActivity::class.java).apply {
            putExtra("DESTINATION", destination)
        }
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Set up map settings, markers, etc.
        Log.d("MainActivity", "Map is ready")
    }

    override fun onJourneyStarted() {
        showMainActivityElements()
    }

    private fun showMainActivityElements() {
        Log.d("MainActivity", "Showing main activity elements")
        fragmentContainer.visibility = View.GONE
        mapFragment.view?.visibility = View.VISIBLE
        searchBarContainer.visibility = View.VISIBLE
        fabLocation.visibility = View.VISIBLE
        fabEmergency.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE
    }

    private fun loadFragment(fragment: Fragment) {
        Log.d("MainActivity", "Loading fragment: ${fragment.javaClass.simpleName}")
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        // Show fragment container and hide other UI elements
        fragmentContainer.visibility = View.VISIBLE
        mapFragment.view?.visibility = View.GONE
        searchBarContainer.visibility = View.GONE
        fabLocation.visibility = View.GONE
        fabEmergency.visibility = View.GONE
        bottomNavigation.visibility = View.GONE

        // Add a callback for when the fragment is popped from the back stack
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                showMainActivityElements()
            }
        }
    }

    private fun checkLocationPermissionAndShowUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            showUserLocation()
        }
    }

    private fun showUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    userMarker?.remove()
                    userMarker = mMap.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    Log.d("MainActivity", "User location shown: $userLatLng")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUserLocation()
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun processVehicleData(destination: String, vehicleType: String, vehiclePlate: String) {
        // Implement the logic to process the vehicle data
        // This method should be defined elsewhere in your code
    }
}