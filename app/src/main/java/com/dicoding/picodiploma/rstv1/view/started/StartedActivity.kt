package com.dicoding.picodiploma.rstv1.view.started

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.picodiploma.rstv1.databinding.ActivityStartedBinding
import com.dicoding.picodiploma.rstv1.view.welcome.WelcomeActivity
class StartedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

                setupAction()
            }
    private fun setupAction() {
        binding.getStartedButton.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }

}