package com.dicoding.picodiploma.rstv1.view.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.rstv1.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var profileImage: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var rvRecentJourneys: RecyclerView
    private lateinit var btnLogout: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImage = view.findViewById(R.id.profileImage)
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        rvRecentJourneys = view.findViewById(R.id.rvRecentJourneys)
        btnLogout = view.findViewById(R.id.btnLogout)

        // Set up RecyclerView
        rvRecentJourneys.layoutManager = LinearLayoutManager(context)
        val journeyAdapter = JourneyAdapter(getFakeJourneys())
        rvRecentJourneys.adapter = journeyAdapter

        // Set up click listener for logout button
        btnLogout.setOnClickListener {
            // Handle logout
        }

        // Load user data
        loadUserData()
    }

    private fun loadUserData() {
        tvName.text = "Name"
        tvEmail.text = "YourEmail@example.com"
        tvPhone.text = "YourNumber(+62)"
        // Load profile image
        // profileImage.setImageResource(R.drawable.profile_placeholder)
    }

    private fun getFakeJourneys(): List<Journey> {
        return listOf(
            Journey("Curug Citambur", "120 km", "1 jam 49 menit", "Mobil"),
            Journey("Pantai Pangandaran", "223 km", "4 jam 30 menit", "Mobil"),
            Journey("Gunung Papandayan", "180 km", "3 jam 15 menit", "Mobil"),
            Journey("Kawah Putih", "47 km", "1 jam 10 menit", "Mobil"),
            Journey("Situ Patenggang", "50 km", "1 jam 20 menit", "Mobil")
        )
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}

data class Journey(
    val destination: String,
    val distance: String,
    val duration: String,
    val transportMode: String
)

class JourneyAdapter(private val journeys: List<Journey>) :
    RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder>() {

    class JourneyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDestination: TextView = view.findViewById(R.id.tvDestination)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journey, parent, false)
        return JourneyViewHolder(view)
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        val journey = journeys[position]
        holder.tvDestination.text = journey.destination
        holder.tvDetails.text = "${journey.distance} - ${journey.duration} (${journey.transportMode})"
    }

    override fun getItemCount() = journeys.size
}