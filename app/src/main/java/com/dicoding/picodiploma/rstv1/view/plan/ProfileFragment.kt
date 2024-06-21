package com.dicoding.picodiploma.rstv1.view.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.rstv1.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Data fake
        val name = "Afrijal Indra"
        val email = "afrijal@gmail.com"
        val userId = "USER123456"

        // Set up profile information
        view.findViewById<ShapeableImageView>(R.id.iv_profile_picture).setImageResource(R.drawable.ic_person_blue)
        view.findViewById<MaterialTextView>(R.id.tv_name).text = name
        view.findViewById<MaterialTextView>(R.id.tv_email).text = email
        view.findViewById<MaterialTextView>(R.id.tv_user_id).text = userId

        // Set up complete profile button (hidden by default)
        val btnCompleteProfile = view.findViewById<MaterialButton>(R.id.btn_complete_profile)
        btnCompleteProfile.visibility = View.GONE

        // Set up logout button
        view.findViewById<MaterialButton>(R.id.btn_logout).setOnClickListener {
            // Implementasi logout
        }

        // Setup RecyclerView untuk riwayat perjalanan
        val rvTripHistory = view.findViewById<RecyclerView>(R.id.rv_trip_history)
        rvTripHistory.layoutManager = LinearLayoutManager(context)
        rvTripHistory.adapter = TripHistoryAdapter(getTripHistoryData())
    }

    // Fungsi untuk mendapatkan data riwayat perjalanan palsu
    private fun getTripHistoryData(): List<TripHistory> {
        return listOf(
            TripHistory("Jakarta - Bandung", "01/01/2024"),
            TripHistory("Surabaya - Malang", "15/01/2024"),
            TripHistory("Yogyakarta - Solo", "30/01/2024")
        )
    }

    // Data class untuk riwayat perjalanan
    data class TripHistory(val route: String, val date: String)

    // Adapter sederhana untuk RecyclerView
    private inner class TripHistoryAdapter(private val tripHistories: List<TripHistory>) :
        RecyclerView.Adapter<TripHistoryAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvRoute: TextView = itemView.findViewById(R.id.tv_route)
            val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_trip, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val tripHistory = tripHistories[position]
            holder.tvRoute.text = tripHistory.route
            holder.tvDate.text = tripHistory.date
        }

        override fun getItemCount() = tripHistories.size
    }
}