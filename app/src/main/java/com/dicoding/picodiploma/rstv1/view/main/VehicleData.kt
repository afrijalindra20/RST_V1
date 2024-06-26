package com.dicoding.picodiploma.rstv1.view.main

data class VehicleData(
    val type: String,
    val fullTankCapacity: Double,
    val fuelEfficiency: Double,
    var currentFuelLevel: String
)