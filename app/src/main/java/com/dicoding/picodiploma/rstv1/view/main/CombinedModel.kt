package com.dicoding.picodiploma.rstv1.view.main

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.*

class CombinedModel(context: Context) {
    private val interpreter: Interpreter
    private val geocoder: Geocoder

    init {
        val modelFile = loadModelFile(context)
        val options = Interpreter.Options()
        interpreter = Interpreter(modelFile, options)
        geocoder = Geocoder(context, Locale.getDefault())
    }

    private fun loadModelFile(context: Context): ByteBuffer {
        val fileName = "combined_model.tflite"
        val assetFileDescriptor = context.assets.openFd(fileName)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predictRoute(startPoint: LatLng, endPoint: LatLng, tankCapacity: Float, fuelEfficiency: Float): RouteRecommendations {
        val input = prepareInput(startPoint, endPoint, tankCapacity, fuelEfficiency)
        val output = predictFromModel(input)
        return parseOutput(output, tankCapacity, fuelEfficiency)
    }

    private fun prepareInput(startPoint: LatLng, endPoint: LatLng, tankCapacity: Float, fuelEfficiency: Float): FloatArray {
        return floatArrayOf(
            startPoint.latitude.toFloat(),
            startPoint.longitude.toFloat(),
            endPoint.latitude.toFloat(),
            endPoint.longitude.toFloat(),
            tankCapacity,
            fuelEfficiency
        )
    }

    private fun predictFromModel(input: FloatArray): FloatArray {
        val inputBuffer = ByteBuffer.allocateDirect(input.size * 4).order(ByteOrder.nativeOrder())
        inputBuffer.asFloatBuffer().put(input)

        val outputBuffer = ByteBuffer.allocateDirect(5 * 4).order(ByteOrder.nativeOrder())
        interpreter.run(inputBuffer, outputBuffer)

        val output = FloatArray(5)
        outputBuffer.asFloatBuffer().get(output)
        return output
    }

    private fun parseOutput(output: FloatArray, tankCapacity: Float, fuelEfficiency: Float): RouteRecommendations {
        val distance = output[0]
        val travelTime = output[1]
        val spbuRecommendation = calculateSpbuRecommendation(tankCapacity, fuelEfficiency)
        return RouteRecommendations(
            distance = distance,
            travelTime = travelTime,
            spbuRecommendation = spbuRecommendation,
            restStopRecommendation = output[3],
            accommodationRecommendation = output[4]
        )
    }

    private fun calculateSpbuRecommendation(tankCapacity: Float, fuelEfficiency: Float): Float {
        return tankCapacity * fuelEfficiency * 0.8f
    }

    fun convertAddressToLatLng(address: String): LatLng? {
        return try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                LatLng(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    data class RouteRecommendations(
        val distance: Float,
        val travelTime: Float,
        val spbuRecommendation: Float,
        val restStopRecommendation: Float,
        val accommodationRecommendation: Float
    )
}