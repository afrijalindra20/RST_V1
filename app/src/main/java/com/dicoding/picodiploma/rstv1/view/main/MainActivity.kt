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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.rstv1.R
import com.dicoding.picodiploma.rstv1.view.plan.*
import com.dicoding.picodiploma.rstv1.view.welcome.WelcomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
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
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel
class MainActivity : AppCompatActivity(), OnMapReadyCallback, StartJourneyListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var userMarker: Marker? = null
    private val DEFAULT_ZOOM = 15f

    private lateinit var fragmentContainer: View
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var searchBarContainer: View
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var fabLocation: FloatingActionButton
    private lateinit var fabEmergency: FloatingActionButton
    private lateinit var bottomNavigation: View

    private lateinit var journeyViewModel: JourneyViewModel
    private lateinit var tflite: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Log.d("MainActivity", "onCreate called")

        initializeViews()
        setupAutoCompleteTextView()
        mapFragment.getMapAsync(this)
        initializeAuth()
        setupBottomNavigation()
        setupFABs()

        journeyViewModel = ViewModelProvider(this).get(JourneyViewModel::class.java)
        loadTFLiteModel()
        val btnProfil: ImageButton = findViewById(R.id.btn_profil)
        btnProfil.setOnClickListener {
            showProfileFragment()
        }
    }

    private fun showProfileFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val profileFragment = ProfileFragment.newInstance()
        fragmentTransaction.add(android.R.id.content, profileFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    override fun onJourneyStarted(vehicleType: String, fullTank: Float, kmPerLiter: Float, currentFuelLevel: String) {
        showJourneyInfoPopup(vehicleType, fullTank, kmPerLiter, currentFuelLevel)
        closeCurrentFragment()
    }

    private fun showJourneyInfoPopup(vehicleType: String, fullTank: Float, kmPerLiter: Float, currentFuelLevel: String) {
        val dialogView = layoutInflater.inflate(R.layout.popup_journey_info, null)

        val tvVehicleInfo = dialogView.findViewById<TextView>(R.id.tvVehicleInfo)
        val tvFuelInfo = dialogView.findViewById<TextView>(R.id.tvFuelInfo)

        tvVehicleInfo.text = "Vehicle: $vehicleType\nFuel Efficiency: $kmPerLiter km/liter"
        tvFuelInfo.text = "Full Tank Capacity: $fullTank liters\nCurrent Fuel Level: $currentFuelLevel"

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Journey Information")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                showMainActivityElements()
            }
            .setOnDismissListener {
                showMainActivityElements()
            }
            .show()
    }

    private fun closeCurrentFragment() {
        supportFragmentManager.popBackStack()
        showMainActivityElements()
    }

    private fun initializeViews() {
        try {
            fragmentContainer = findViewById(R.id.fragment_container)
            mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            searchBarContainer = findViewById(R.id.search_bar_container)
            autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
            fabLocation = findViewById(R.id.fab_location)
            fabEmergency = findViewById(R.id.fab_emergency)
            bottomNavigation = findViewById(R.id.bottom_navigation)

            val btnHome: ImageButton = findViewById(R.id.btn_home)
            val btnAdd: ImageButton = findViewById(R.id.btn_add)

            btnHome.setOnClickListener { showLogoutDialog() }
            btnAdd.setOnClickListener { loadFragment(VehicleInputFragment()) }

        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing views: ${e.message}")
            e.printStackTrace()
        }
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

    private fun initializeAuth() {
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupBottomNavigation() {
        findViewById<ImageButton>(R.id.btn_home).setOnClickListener {
            showLogoutDialog()
        }

        findViewById<ImageButton>(R.id.btn_add).setOnClickListener {
            loadFragment(VehicleInputFragment())
        }
    }

    private fun setupFABs() {
        fabEmergency.setOnClickListener {
            showEmergencyDialog()
        }

        fabLocation.setOnClickListener {
            checkLocationPermissionAndShowUserLocation()
        }
    }

    private fun loadTFLiteModel() {
        val assetManager = assets
        val modelFile = assetManager.openFd("combined_model.tflite")
        val fileDescriptor = modelFile.fileDescriptor
        val inputStream = FileInputStream(fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = modelFile.startOffset
        val declaredLength = modelFile.declaredLength
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        tflite = Interpreter(mappedByteBuffer)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation()
        Log.d("MainActivity", "Map is ready")
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            zoomToUserLocation()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
                    userMarker?.remove()
                    userMarker = mMap.addMarker(MarkerOptions().position(currentLatLng).title("Your Location"))
                    Log.d("MainActivity", "User location shown: $currentLatLng")
                } ?: run {
                    requestLocationUpdates()
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
                    userMarker?.remove()
                    userMarker = mMap.addMarker(MarkerOptions().position(currentLatLng).title("Your Location"))
                    fusedLocationClient.removeLocationUpdates(this)
                    break
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation()
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
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

        fragmentContainer.visibility = View.VISIBLE
        mapFragment.view?.visibility = View.GONE
        searchBarContainer.visibility = View.GONE
        fabLocation.visibility = View.GONE
        fabEmergency.visibility = View.GONE
        bottomNavigation.visibility = View.GONE

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                showMainActivityElements()
            }
        }
    }

    private fun checkLocationPermissionAndShowUserLocation() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showLocationPermissionRationale()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun showLocationPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app needs the Location permission to show your current location on the map.")
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            .create()
            .show()
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

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performLogout() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            googleSignInClient.revokeAccess().addOnCompleteListener(this) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun navigateToMenujuActivity(destination: String) {
        val intent = Intent(this, MenujuActivity::class.java).apply {
            putExtra("DESTINATION", destination)
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}