package com.dicoding.picodiploma.rstv1.view.plan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dicoding.picodiploma.rstv1.R

interface StartJourneyListener {
    fun onJourneyStarted(vehicleType: String, fullTank: Float, kmPerLiter: Float, currentFuelLevel: String)
}

class StartJourneyFragment : Fragment() {
    private lateinit var startJourneyButton: Button
    private var startJourneyListener: StartJourneyListener? = null

    private var vehicleType: String? = null
    private var fullTank: Float = 0f
    private var kmPerLiter: Float = 0f
    private var currentFuelLevel: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vehicleType = it.getString("vehicleType")
            fullTank = it.getFloat("fullTank")
            kmPerLiter = it.getFloat("kmPerLiter")
            currentFuelLevel = it.getString("currentFuelLevel")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is StartJourneyListener) {
            startJourneyListener = context
        } else {
            throw RuntimeException("$context must implement StartJourneyListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_journey, container, false)

        startJourneyButton = view.findViewById(R.id.start_journey_button)

        startJourneyButton.setOnClickListener {
            // Tutup semua fragment dan kembali ke tampilan peta utama
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            // Kirim data ke activity melalui interface
            startJourneyListener?.onJourneyStarted(
                vehicleType ?: "",
                fullTank,
                kmPerLiter,
                currentFuelLevel ?: ""
            )
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        startJourneyListener = null
    }
}