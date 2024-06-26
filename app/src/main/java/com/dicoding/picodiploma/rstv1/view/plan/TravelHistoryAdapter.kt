package com.dicoding.picodiploma.rstv1.view.plan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.rstv1.R

class TravelHistoryAdapter(private val travelHistories: List<TravelHistory>) :
    RecyclerView.Adapter<TravelHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val destinationTextView: TextView = view.findViewById(R.id.destinationTextView)
        val distanceTextView: TextView = view.findViewById(R.id.distanceTextView)
        val durationTextView: TextView = view.findViewById(R.id.durationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_travel_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = travelHistories[position]
        holder.destinationTextView.text = history.destination
        holder.distanceTextView.text = "${history.distance} km"
        holder.durationTextView.text = "${history.duration} menggunakan ${history.transportMode}"
    }

    override fun getItemCount() = travelHistories.size
}

data class TravelHistory(
    val destination: String,
    val distance: Int,
    val duration: String,
    val transportMode: String
)