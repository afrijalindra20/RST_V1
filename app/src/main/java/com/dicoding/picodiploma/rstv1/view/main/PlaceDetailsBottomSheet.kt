package com.dicoding.picodiploma.rstv1.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dicoding.picodiploma.rstv1.R
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaceDetailsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_PLACE = "place"
        private const val ARG_INFO_TEXT = "info_text"

        fun newInstance(place: Place): PlaceDetailsBottomSheet {
            val fragment = PlaceDetailsBottomSheet()
            val args = Bundle()
            args.putParcelable(ARG_PLACE, place)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(infoText: String): PlaceDetailsBottomSheet {
            val fragment = PlaceDetailsBottomSheet()
            val args = Bundle()
            args.putString(ARG_INFO_TEXT, infoText)
            fragment.arguments = args
            return fragment
        }
    }

    class PlaceDetailsBottomSheet : BottomSheetDialogFragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.bottom_sheet_place_details, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val textViewDetails = view.findViewById<TextView>(R.id.textViewDetails)

            arguments?.getParcelable<Place>(ARG_PLACE)?.let { place ->
                val detailsText = """
                Nama: ${place.name}
                Alamat: ${place.address}
                Nomor Telepon: ${place.phoneNumber ?: "Tidak tersedia"}
            """.trimIndent()
                textViewDetails.text = detailsText
            } ?: run {
                arguments?.getString(ARG_INFO_TEXT)?.let { infoText ->
                    textViewDetails.text = infoText
                }
            }
        }
    }
}