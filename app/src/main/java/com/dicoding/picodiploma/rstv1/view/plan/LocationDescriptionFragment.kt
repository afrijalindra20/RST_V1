package com.dicoding.picodiploma.rstv1.view.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.picodiploma.rstv1.R

class LocationDescriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi view lain dan set up listener jika diperlukan
        // Misalnya:
        // val textViewName = view.findViewById<TextView>(R.id.textViewName)
        // val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        //
        // textViewName.text = "Nama Lokasi"
        // ratingBar.rating = 4.5f
    }
}