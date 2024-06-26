package com.dicoding.picodiploma.rstv1.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.rstv1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NavigationFragment : BottomSheetDialogFragment() {
    private lateinit var mMap: GoogleMap
    private lateinit var geoApiContext: GeoApiContext
    private lateinit var origin: LatLng
    private lateinit var destination: LatLng

    companion object {
        fun newInstance(origin: LatLng, destination: LatLng): NavigationFragment {
            val fragment = NavigationFragment()
            fragment.arguments = Bundle().apply {
                putParcelable("origin", origin)
                putParcelable("destination", destination)
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        origin = arguments?.getParcelable("origin")!!
        destination = arguments?.getParcelable("destination")!!
        geoApiContext = GeoApiContext.Builder()
            .apiKey("YOUR_API_KEY")
            .build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.navigation_map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            setupMap()
            getDirections()
        }
    }

    private fun setupMap() {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(origin).title("Start"))
        mMap.addMarker(MarkerOptions().position(destination).title("Destination"))
        val bounds = LatLngBounds.Builder().include(origin).include(destination).build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun getDirections() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .await()

                withContext(Dispatchers.Main) {
                    addPolyline(result)
                    showDirectionInfo(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addPolyline(result: com.google.maps.model.DirectionsResult) {
        val decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.encodedPath)
        mMap.addPolyline(PolylineOptions().addAll(decodedPath))
    }

    private fun showDirectionInfo(result: com.google.maps.model.DirectionsResult) {
        val route = result.routes[0]
        val leg = route.legs[0]
        val duration = leg.duration.humanReadable
        val distance = leg.distance.humanReadable

        val infoText = """
            Distance: $distance
            Duration: $duration
            Fastest route: ${route.summary}
        """.trimIndent()

        view?.findViewById<TextView>(R.id.direction_info)?.text = infoText
    }
}