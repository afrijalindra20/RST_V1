package com.dicoding.picodiploma.rstv1.view.main

import com.google.android.gms.maps.model.LatLng

data class RouteResponse(val route: List<LatLng>, val places: List<Place>)
data class NearestPlaceResponse(val place: Place)
data class Place(
    val name: String,
    val location: LatLng,
    val type: String,
    val distance: Float
)