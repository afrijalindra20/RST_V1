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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val emailInput = view.findViewById<TextInputEditText>(R.id.ed_forgot_email)
        val oldPasswordInput = view.findViewById<TextInputEditText>(R.id.ed_forgot_old_password)
        val newPasswordInput = view.findViewById<TextInputEditText>(R.id.ed_forgot_new_password)
        val confirmPasswordInput = view.findViewById<TextInputEditText>(R.id.ed_forgot_confirm_password)
        val resetButton = view.findViewById<Button>(R.id.ResetPasswordButton)

        resetButton.setOnClickListener {
            val email = emailInput.text.toString()
            val oldPassword = oldPasswordInput.text.toString()
            val newPassword = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (newPassword == confirmPassword) {
                if (oldPassword.isNotEmpty()) {
                    resetPassword(email, oldPassword, newPassword)
                } else {
                    Toast.makeText(context, "Please enter your old password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "New passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String, oldPassword: String, newPassword: String) {
        val user = auth.currentUser
        if (user != null && user.email == email) {
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            fragmentManager?.popBackStack()
                        } else {
                            Toast.makeText(context, "Failed to update password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Authentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "User not found or email doesn't match", Toast.LENGTH_SHORT).show()
        }
    }
}