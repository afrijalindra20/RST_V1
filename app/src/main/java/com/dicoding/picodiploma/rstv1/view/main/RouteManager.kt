package com.dicoding.picodiploma.rstv1.view.main

import com.google.android.gms.maps.model.LatLng

class RouteManager(
    private val mapFragment: MapFragment,
    private val tfliteModel: TFLiteModel,
    private val apiService: APIService
) {
    suspend fun planRoute(
        start: LatLng,
        destination: String,
        vehicleType: String,
        fullTankCapacity: Float,
        fuelEfficiency: Float
    ) {
        val routeResponse = apiService.getRoute(start, destination, vehicleType)
        val recommendations = tfliteModel.calculateRecommendations(
            start,
            routeResponse.route.last(),
            fullTankCapacity,
            fuelEfficiency
        )

        val spbuDistance = fullTankCapacity * fuelEfficiency * 0.8f // 80% dari kapasitas tangki penuh
        val filteredPlaces = filterPlaces(routeResponse.places, recommendations, spbuDistance)
        mapFragment.updateMap(routeResponse.route, filteredPlaces)
    }

    private fun filterPlaces(places: List<Place>, recommendations: Recommendations, spbuDistance: Float): List<Place> {
        return places.filter { place ->
            when (place.type) {
                "gas_station" -> place.distance <= spbuDistance
                "rest_area" -> place.distance <= recommendations.restStopRecommendation
                "lodging" -> place.distance <= recommendations.lodgingRecommendation
                else -> false
            }
        }
    }

    suspend fun findNearestEmergency(position: LatLng, type: String) {
        val nearestPlace = apiService.findNearest(position, type)
        mapFragment.updateMap(listOf(position, nearestPlace.place.location), listOf(nearestPlace.place))
    }

    // Fungsi pembantu untuk menghitung jarak antara dua titik LatLng
    private fun calculateDistance(start: LatLng, end: LatLng): Float {
        val R = 6371 // Radius Bumi dalam kilometer
        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return (R * c).toFloat()
    }
}