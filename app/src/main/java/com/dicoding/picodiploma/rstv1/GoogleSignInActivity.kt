package com.dicoding.picodiploma.rstv1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class GoogleSignInActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "GoogleSignInActivity"
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var emailEditText: EditText
    private lateinit var emailSignUpButton: Button
    private lateinit var googleSignUpButton: Button
    private lateinit var termsTextView: TextView

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        emailEditText = findViewById(R.id.emailEditText)
        emailSignUpButton = findViewById(R.id.emailSignUpButton)
        googleSignUpButton = findViewById(R.id.googleSignUpButton)
        termsTextView = findViewById(R.id.termsTextView)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Email Sign Up Button Click Listener
        emailSignUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (isValidEmail(email)) {
                signUpWithEmail(email)
            } else {
                emailEditText.error = getString(R.string.error_invalid_email)
            }
        }

        // Google Sign Up Button Click Listener
        googleSignUpButton.setOnClickListener { signInWithGoogle() }

        // Make Terms of Service and Privacy Policy clickable
        setupClickableTerms()
    }

    private fun signUpWithEmail(email: String) {
        // Implement your email sign-up logic here
        Toast.makeText(this, getString(R.string.msg_email_signup, email), Toast.LENGTH_SHORT).show()
    }

    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(completedTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email
            Toast.makeText(this, getString(R.string.msg_google_signin_success, email), Toast.LENGTH_SHORT).show()
            // TODO: Send email to your server or continue with app's flow
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            Toast.makeText(this, getString(R.string.error_google_signin), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickableTerms() {
        val fullText = getString(R.string.text_terms_and_privacy)
        val spannableString = SpannableString(fullText)

        val termsSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openWebPage(getString(R.string.url_terms_of_service))
            }
        }

        val policySpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openWebPage(getString(R.string.url_privacy_policy))
            }
        }

        val termsStart = fullText.indexOf(getString(R.string.link_terms_of_service))
        val termsEnd = termsStart + getString(R.string.link_terms_of_service).length
        val policyStart = fullText.indexOf(getString(R.string.link_privacy_policy))
        val policyEnd = policyStart + getString(R.string.link_privacy_policy).length

        spannableString.setSpan(termsSpan, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(policySpan, policyStart, policyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        termsTextView.text = spannableString
        termsTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}