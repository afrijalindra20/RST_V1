package com.dicoding.picodiploma.rstv1.view.welcome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.rstv1.R
import com.dicoding.picodiploma.rstv1.view.main.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = Firebase.auth

        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in, go directly to MainActivity
            startMainActivity()
            return
        }

        // Initialize UI elements
        val loginButton = findViewById<Button>(R.id.LoginButton)
        val registerTextView = findViewById<TextView>(R.id.textView2)
        val forgotPasswordTextView = findViewById<TextView>(R.id.textView3)
        val emailInput = findViewById<TextInputEditText>(R.id.ed_login_email)
        val passwordInput = findViewById<TextInputEditText>(R.id.ed_login_password)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            loginUser(email, password)
        }

        registerTextView.setOnClickListener {
            showRegistrationFragment()
        }

        forgotPasswordTextView.setOnClickListener {
            showForgotPasswordFragment()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Login successful.", Toast.LENGTH_SHORT).show()
                    startMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // This closes the WelcomeActivity
    }

    private fun showRegistrationFragment() {
        val fragment = RegistrationFragment()
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showForgotPasswordFragment() {
        val fragment = ForgotFragment()
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }
}