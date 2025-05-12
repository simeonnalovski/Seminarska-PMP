package com.ssr.bmicalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.ssr.bmicalculator.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupGoogleSignIn()
        setupClickListeners()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupClickListeners() {
        binding.btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.btnAnonymousLogin.setOnClickListener {
            auth.signInAnonymously()
                .addOnSuccessListener {
                    saveUserToFirestore(it.user)
                    goToMain()
                }
                .addOnFailureListener {
                    showToast("Anonymous login failed.")
                }
        }

        binding.registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding.btnEmailLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email and password must not be empty.")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    saveUserToFirestore(it.user)
                    goToMain()
                }
                .addOnFailureListener {
                    showToast("Login failed: ${it.message}")
                }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        saveUserToFirestore(it.user)
                        goToMain()
                    }
                    .addOnFailureListener {
                        showToast("Google auth failed: ${it.message}")
                    }
            } catch (e: ApiException) {
                showToast("Google sign-in failed: ${e.message}")
            }
        }
    }

    private fun saveUserToFirestore(user: FirebaseUser?) {
        user?.let {
            val userData = hashMapOf(
                "uid" to it.uid,
                "email" to it.email,
                "isAnonymous" to it.isAnonymous,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(it.uid)
                .set(userData)
                .addOnSuccessListener {
                    Log.d("Firestore", "User saved.")
                }
                .addOnFailureListener { error ->
                    Log.e("Firestore", "Failed to save user.", error)
                }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
