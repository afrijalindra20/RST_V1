package com.dicoding.picodiploma.rstv1.view.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.picodiploma.rstv1.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        // Set the background to be semi-transparent
        view.setBackgroundResource(R.color.semi_transparent_background)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        // Initialize UI elements
        val emailInput = view.findViewById<TextInputEditText>(R.id.ed_register_email)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.ed_register_password)
        val confirmPasswordInput = view.findViewById<TextInputEditText>(R.id.ed_register_confirm_password)
        val registerButton = view.findViewById<Button>(R.id.RegisterButton)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (password == confirmPassword) {
                registerUser(email, password)
            } else {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration success
                    Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                    // You can navigate back to the login screen or automatically log the user in
                    fragmentManager?.popBackStack()
                } else {
                    // If registration fails, display a message to the user.
                    Toast.makeText(context, "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}