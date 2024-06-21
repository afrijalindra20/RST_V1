package com.dicoding.picodiploma.rstv1.view.plan

import android.content.Context
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
            saveFuelLevel(fuelLevel)
            navigateToStartJourneyFragment()
        }

        fullTankButton.setOnClickListener(clickListener)
        halfTankButton.setOnClickListener(clickListener)
        almostEmptyButton.setOnClickListener(clickListener)

        return view
    }

    private fun saveFuelLevel(fuelLevel: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("VehicleInfo", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("currentFuelLevel", fuelLevel).apply()
    }

    private fun navigateToStartJourneyFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StartJourneyFragment())
            .addToBackStack(null)
            .commit()
    }
}