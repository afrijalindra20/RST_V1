package com.dicoding.picodiploma.rstv1.view.plan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.dicoding.picodiploma.rstv1.R
import com.google.android.gms.maps.model.LatLng
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MlProcessingFragment : Fragment() {

    private lateinit var tflite: Interpreter
    private lateinit var tvJarak: TextView
    private lateinit var tvWaktuTempuh: TextView
    private lateinit var tvRekomendasiSPBU: TextView
    private lateinit var tvRekomendasiIstirahat: TextView
    private lateinit var tvRekomendasiPenginapan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ml_processing, container, false)

        tvJarak = view.findViewById(R.id.tv_jarak)
        tvWaktuTempuh = view.findViewById(R.id.tv_waktu_tempuh)
        tvRekomendasiSPBU = view.findViewById(R.id.tv_rekomendasi_spbu)
        tvRekomendasiIstirahat = view.findViewById(R.id.tv_rekomendasi_istirahat)
        tvRekomendasiPenginapan = view.findViewById(R.id.tv_rekomendasi_penginapan)

        // Ambil data dari arguments (asumsi data dikirim melalui Bundle)
        arguments?.let { args ->
            val vehicleType = args.getString("vehicleType", "car")
            val fullTank = args.getFloat("fullTank", 0f)
            val kmPerLiter = args.getFloat("kmPerLiter", 0f)
            val startLat = args.getDouble("startLat", 0.0)
            val startLng = args.getDouble("startLng", 0.0)
            val endLat = args.getDouble("endLat", 0.0)
            val endLng = args.getDouble("endLng", 0.0)

            val startCoordinate = LatLng(startLat, startLng)
            val endCoordinate = LatLng(endLat, endLng)

            // Proses data menggunakan model ML
            val result = processMLModel(vehicleType, fullTank, kmPerLiter, startCoordinate, endCoordinate)

            // Tampilkan hasil
            showResults(result)
        }

        return view
    }

    private fun loadModel() {
        try {
            val modelFile = File(context?.filesDir, "combined_model.tflite")
            if (!modelFile.exists()) {
                val inputStream = context?.assets?.open("combined_model.tflite")
                inputStream?.use { input ->
                    modelFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            Log.d("MlProcessingFragment", "Model file path: ${modelFile.absolutePath}")
            Log.d("MlProcessingFragment", "Model file exists: ${modelFile.exists()}")

            val tfliteOptions = Interpreter.Options()
            tflite = Interpreter(modelFile, tfliteOptions)

        } catch (e: IOException) {
            Log.e("MlProcessingFragment", "Error loading model: ${e.message}", e)
            Toast.makeText(context, "Gagal memuat model: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun processMLModel(vehicleType: String, fullTank: Float, kmPerLiter: Float, startCoordinate: LatLng, endCoordinate: LatLng): Map<String, Any> {
        // Konversi input menjadi format yang sesuai untuk model ML
        val input = floatArrayOf(
            if (vehicleType == "car") 1f else 0f,
            fullTank,
            kmPerLiter,
            startCoordinate.latitude.toFloat(),
            startCoordinate.longitude.toFloat(),
            endCoordinate.latitude.toFloat(),
            endCoordinate.longitude.toFloat()
        )

        // Jalankan inferensi
        val output = FloatArray(2) // Asumsi output model adalah jarak dan waktu tempuh
        tflite.run(input, output)

        val jarak = output[0]
        val waktuTempuh = output[1]
        val rekomendasiSPBU = calculateSPBURecommendation(jarak, fullTank, kmPerLiter)
        val rekomendasiTempatIstirahat = calculateRestRecommendation(waktuTempuh)
        val rekomendasiPenginapan = calculateLodgingRecommendation(waktuTempuh)

        return mapOf(
            "jarak" to jarak,
            "waktuTempuh" to waktuTempuh,
            "rekomendasiSPBU" to rekomendasiSPBU,
            "rekomendasiTempatIstirahat" to rekomendasiTempatIstirahat,
            "rekomendasiPenginapan" to rekomendasiPenginapan
        )
    }

    private fun calculateSPBURecommendation(jarak: Float, fullTank: Float, kmPerLiter: Float): Float {
        val jarakMaxTanpaIsi = fullTank * kmPerLiter
        return jarakMaxTanpaIsi - (jarakMaxTanpaIsi * 0.2f)
    }

    private fun calculateRestRecommendation(waktuTempuh: Float): String {
        return when {
            waktuTempuh < 120 -> "Tidak perlu istirahat"
            waktuTempuh < 240 -> "Istirahat sekali"
            else -> "Istirahat dua kali atau lebih"
        }
    }

    private fun calculateLodgingRecommendation(waktuTempuh: Float): String {
        return if (waktuTempuh > 480) "Disarankan menginap" else "Tidak perlu menginap"
    }

    private fun showResults(result: Map<String, Any>) {
        tvJarak.text = "Jarak: ${result["jarak"]} km"
        tvWaktuTempuh.text = "Waktu Tempuh: ${result["waktuTempuh"]} menit"
        tvRekomendasiSPBU.text = "Rekomendasi SPBU: Isi bensin setiap ${result["rekomendasiSPBU"]} km"
        tvRekomendasiIstirahat.text = "Rekomendasi Istirahat: ${result["rekomendasiTempatIstirahat"]}"
        tvRekomendasiPenginapan.text = "Rekomendasi Penginapan: ${result["rekomendasiPenginapan"]}"
    }
}