package com.dicoding.picodiploma.rstv1.view.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.dicoding.picodiploma.rstv1.R

class FuelLevelFragment : Fragment() {
    private lateinit var fullTankButton: Button
    private lateinit var halfTankButton: Button
    private lateinit var almostEmptyButton: Button

    // Variabel untuk menyimpan data dari VehicleInputFragment
    private var vehicleType: String? = null
    private var fullTank: Float = 0f
    private var kmPerLiter: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mengambil data dari arguments yang dikirim oleh VehicleInputFragment
        arguments?.let {
            vehicleType = it.getString("vehicleType")
            fullTank = it.getFloat("fullTank")
            kmPerLiter = it.getFloat("kmPerLiter")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_fuel_level, container, false)

        fullTankButton = view.findViewById(R.id.full_tank_button)
        halfTankButton = view.findViewById(R.id.half_tank_button)
        almostEmptyButton = view.findViewById(R.id.almost_empty_button)

        val clickListener = View.OnClickListener { clickedView ->
            val fuelLevel = when (clickedView.id) {
                R.id.full_tank_button -> "Full Tank"
                R.id.half_tank_button -> "Half Tank"
                R.id.almost_empty_button -> "Almost Empty"
                else -> ""
            }
            navigateToStartJourneyFragment(fuelLevel)
        }

        fullTankButton.setOnClickListener(clickListener)
        halfTankButton.setOnClickListener(clickListener)
        almostEmptyButton.setOnClickListener(clickListener)

        return view
    }

    private fun navigateToStartJourneyFragment(fuelLevel: String) {
        val startJourneyFragment = StartJourneyFragment().apply {
            arguments = Bundle().apply {
                putString("vehicleType", vehicleType)
                putFloat("fullTank", fullTank)
                putFloat("kmPerLiter", kmPerLiter)
                putString("currentFuelLevel", fuelLevel)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, startJourneyFragment)
            .addToBackStack(null)
            .commit()
    }
}