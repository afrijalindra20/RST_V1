package com.dicoding.picodiploma.rstv1.view.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.picodiploma.rstv1.R
import com.google.android.material.textfield.TextInputEditText

class VehicleInputFragment : Fragment() {
    private lateinit var vehicleTypeInput: TextInputEditText
    private lateinit var fullTankInput: TextInputEditText
    private lateinit var kmPerLiterInput: TextInputEditText
    private lateinit var nextButton: Button
    private lateinit var closeButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_input, container, false)

        vehicleTypeInput = view.findViewById(R.id.ed_vehicle_type)
        fullTankInput = view.findViewById(R.id.ed_full_tank)
        kmPerLiterInput = view.findViewById(R.id.ed_km_per_liter)
        nextButton = view.findViewById(R.id.nextButton)
        closeButton = view.findViewById(R.id.closeButton)

        nextButton.setOnClickListener {
            if (validateInputs()) {
                navigateToFuelLevelFragment()
            }
        }

        closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun validateInputs(): Boolean {
        val vehicleType = vehicleTypeInput.text.toString()
        val fullTank = fullTankInput.text.toString()
        val kmPerLiter = kmPerLiterInput.text.toString()

        if (vehicleType.isEmpty() || fullTank.isEmpty() || kmPerLiter.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (fullTank.toFloatOrNull() == null || kmPerLiter.toFloatOrNull() == null) {
            Toast.makeText(context, "Please enter valid numbers for tank capacity and fuel consumption", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun navigateToFuelLevelFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FuelLevelFragment())
            .addToBackStack(null)
            .commit()
    }
}