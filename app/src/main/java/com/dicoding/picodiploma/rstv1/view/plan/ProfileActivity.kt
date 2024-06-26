package com.dicoding.picodiploma.rstv1.view.plan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.rstv1.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val travelHistories = listOf(
            TravelHistory("Curug Citambur", 120, "1 jam 49 menit", "mobil"),
            TravelHistory("Curug Citambur", 120, "1 jam 49 menit", "mobil"),
            TravelHistory("Curug Citambur", 120, "1 jam 49 menit", "mobil")
        )

        val recyclerView: RecyclerView = findViewById(R.id.travelHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TravelHistoryAdapter(travelHistories)
    }
}