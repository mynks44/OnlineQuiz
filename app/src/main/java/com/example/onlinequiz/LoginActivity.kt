package com.example.onlinequiz

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Users")
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val savedEmailOrUsername = sharedPreferences.getString("emailOrUsername", "")
        val savedPassword = sharedPreferences.getString("password", "")
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            binding.etEmail.setText(savedEmailOrUsername)
            binding.etPassword.setText(savedPassword)
            binding.rememberMeCheckBox.isChecked = true
        }

        binding.btnLogin.setOnClickListener {
            val input = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username/email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (input.contains("@")) {
                loginWithEmail(input, password)
            } else {
                loginWithUsername(input, password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to send reset email: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData(email, password)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginWithUsername(username: String, password: String) {
        database.child("Usernames").child(username).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userId = snapshot.value.toString()
                database.child("Users").child(userId).get().addOnSuccessListener { userSnapshot ->
                    val email = userSnapshot.child("email").value.toString()
                    loginWithEmail(email, password)
                }
            } else {
                Toast.makeText(this, "Invalid username!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData(emailOrUsername: String, password: String) {
        val editor = sharedPreferences.edit()
        if (binding.rememberMeCheckBox.isChecked) {
            editor.putString("emailOrUsername", emailOrUsername)
            editor.putString("password", password)
            editor.putBoolean("rememberMe", true)
        } else {
            editor.clear()
        }
        editor.apply()
    }
}
