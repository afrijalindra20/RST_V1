package com.dicoding.picodiploma.rstv1.view.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.dicoding.picodiploma.rstv1.R
import com.dicoding.picodiploma.rstv1.view.started.StartedActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Delay selama 3 detik sebelum berpindah ke MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, StartedActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}