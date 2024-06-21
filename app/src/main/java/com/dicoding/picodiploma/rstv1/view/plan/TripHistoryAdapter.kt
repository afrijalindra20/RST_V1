package com.dicoding.picodiploma.rstv1.view.plan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.rstv1.R

class TripHistoryAdapter(private val tripHistoryList: List<TripHistory>) :
    RecyclerView.Adapter<TripHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startLocationTextView: TextView = itemView.findViewById(R.id.start_location_text_view)
        val endLocationTextView: TextView = itemView.findViewById(R.id.end_location_text_view)
        val distanceTextView: TextView = itemView.findViewById(R.id.distance_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tripHistory = tripHistoryList[position]
        holder.startLocationTextView.text = tripHistory.startLocation
        holder.endLocationTextView.text = tripHistory.endLocation
        holder.distanceTextView.text = tripHistory.distance.toString()
    }

    override fun getItemCount(): Int {
        return tripHistoryList.size
    }
}