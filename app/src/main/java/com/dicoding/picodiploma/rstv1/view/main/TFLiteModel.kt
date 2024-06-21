package com.dicoding.picodiploma.rstv1.view.main

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteModel(context: Context) {
    private val interpreter: Interpreter

    init {
        val model = FileUtil.loadMappedFile(context, "combined_model.tflite")
        interpreter = Interpreter(model)
    }

    fun predict(input: FloatArray): FloatArray {
        val inputBuffer = ByteBuffer.allocateDirect(input.size * 4).order(ByteOrder.nativeOrder())
        inputBuffer.asFloatBuffer().put(input)

        val outputBuffer = ByteBuffer.allocateDirect(5 * 4).order(ByteOrder.nativeOrder())
        interpreter.run(inputBuffer, outputBuffer)

        val output = FloatArray(5)
        outputBuffer.asFloatBuffer().get(output)
        return output
    }

    fun calculateRecommendations(
        startCoord: LatLng,
        endCoord: LatLng,
        fullTankCapacity: Float,
        fuelEfficiency: Float
    ): Recommendations {
        val input = floatArrayOf(
            startCoord.latitude.toFloat(), startCoord.longitude.toFloat(),
            endCoord.latitude.toFloat()
        )
        val inputSize = input.size * 4 // 4 bytes per float
        Log.d("TFLiteModel", "Input size: $inputSize bytes")

        val output = predict(input)

        return Recommendations(
            distance = output[0],
            duration = output[1],
            spbuRecommendation = output[2],
            restStopRecommendation = output[3],
            lodgingRecommendation = output[4]
        )
    }
}

data class Recommendations(
    val distance: Float,
    val duration: Float,
    val spbuRecommendation: Float,
    val restStopRecommendation: Float,
    val lodgingRecommendation: Float
)