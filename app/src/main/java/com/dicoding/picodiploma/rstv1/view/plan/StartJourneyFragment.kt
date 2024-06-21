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
    fun onJourneyStarted()
}

class StartJourneyFragment : Fragment() {
    private lateinit var startJourneyButton: Button
    private var startJourneyListener: StartJourneyListener? = null

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
            // Here you can add any logic needed before starting the journey
            // For example, you might want to save the journey start time

            // Close all fragments and return to the main map view
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            // Notify the activity that the journey has started
            startJourneyListener?.onJourneyStarted()
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        startJourneyListener = null
    }
}