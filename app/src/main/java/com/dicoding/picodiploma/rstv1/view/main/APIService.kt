package com.dicoding.picodiploma.rstv1.view.main

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

class APIService {
    private val cities = mapOf(
        "Jakarta" to LatLng(-6.2088, 106.8456),
        "Bandung" to LatLng(-6.9175, 107.6191),
        "Surabaya" to LatLng(-7.2575, 112.7521),
        "Yogyakarta" to LatLng(-7.7956, 110.3695),
        "Semarang" to LatLng(-6.9932, 110.4203),
        "Bogor" to LatLng(-6.5971, 106.8060)
    )

    // Metode getter publik untuk cities
    fun getCity(name: String): LatLng? {
        return cities[name]
    }

    suspend fun getRoute(start: LatLng, destination: String, vehicleType: String): RouteResponse {
        delay(1000) // Simulate API delay

        val endLatLng = cities[destination] ?: generateRandomEndPoint(start)
        val route = generateFakeRoute(start, endLatLng)
        val distance = calculateRouteDistance(route)

        val places = generateFakePlaces(route, distance)

        // Kita hanya menggunakan route dan places, mengabaikan distance
        return RouteResponse(route, places)
    }

    suspend fun findNearest(position: LatLng, type: String): NearestPlaceResponse {
        delay(500) // Simulate API delay

        val place = Place(
            "Nearest $type",
            LatLng(position.latitude + Random.nextDouble(-0.01, 0.01),
                position.longitude + Random.nextDouble(-0.01, 0.01)),
            type,
            Random.nextFloat() * 10
        )

        return NearestPlaceResponse(place)
    }

    private fun generateRandomEndPoint(start: LatLng): LatLng {
        return LatLng(
            start.latitude + Random.nextDouble(-0.5, 0.5),
            start.longitude + Random.nextDouble(-0.5, 0.5)
        )
    }

    private fun generateFakeRoute(start: LatLng, end: LatLng): List<LatLng> {
        val steps = 20
        val latStep = (end.latitude - start.latitude) / steps
        val lngStep = (end.longitude - start.longitude) / steps

        return (0..steps).map { i ->
            LatLng(
                start.latitude + i * latStep + Random.nextDouble(-0.01, 0.01),
                start.longitude + i * lngStep + Random.nextDouble(-0.01, 0.01)
            )
        }
    }

    // Mengubah calculateRouteDistance menjadi publik
    fun calculateRouteDistance(route: List<LatLng>): Float {
        var distance = 0f
        for (i in 0 until route.size - 1) {
            distance += calculateDistance(route[i], route[i+1])
        }
        return distance
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Float {
        val R = 6371 // Earth radius in kilometers
        val dLat = Math.toRadians(abs(end.latitude - start.latitude))
        val dLon = Math.toRadians(abs(end.longitude - start.longitude))
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(start.latitude)) * Math.cos(Math.toRadians(end.latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (R * c).toFloat()
    }

    private fun generateFakePlaces(route: List<LatLng>, totalDistance: Float): List<Place> {
        val places = mutableListOf<Place>()
        val numGasStations = (totalDistance / 50).toInt().coerceAtLeast(1)
        val numRestAreas = (totalDistance / 100).toInt().coerceAtLeast(1)
        val numHotels = (totalDistance / 200).toInt().coerceAtLeast(1)

        for (i in 1..numGasStations) {
            val index = (route.size * i / (numGasStations + 1)).coerceAtMost(route.size - 1)
            places.add(Place("SPBU ${i}", route[index], "gas_station", calculateDistance(route[0], route[index])))
        }

        for (i in 1..numRestAreas) {
            val index = (route.size * i / (numRestAreas + 1)).coerceAtMost(route.size - 1)
            places.add(Place("Rest Area ${i}", route[index], "rest_area", calculateDistance(route[0], route[index])))
        }

        for (i in 1..numHotels) {
            val index = (route.size * i / (numHotels + 1)).coerceAtMost(route.size - 1)
            places.add(Place("Hotel ${i}", route[index], "lodging", calculateDistance(route[0], route[index])))
        }

        return places
    }
}