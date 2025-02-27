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
import com.google.firebase.database.ktx.getValue

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().getReference("Users")

        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            binding.etEmail.setText(savedEmail)
            binding.etPassword.setText(savedPassword)
            binding.rememberMeCheckBox.isChecked = true
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        // Fetch user data from the database
                        userId?.let {
                            database.child(it).get().addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    val firstName = snapshot.child("firstName").getValue<String>()
                                    val editor = sharedPreferences.edit()

                                    if (binding.rememberMeCheckBox.isChecked) {
                                        editor.putString("email", email)
                                        editor.putString("password", password)
                                        editor.putBoolean("rememberMe", true)
                                    } else {
                                        editor.clear()
                                    }
                                    editor.apply()

                                    // Store the first name in SharedPreferences for later use
                                    editor.putString("firstName", firstName)
                                    editor.apply()

                                    // Show first name in the navigation bar (or any UI component)
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("firstName", firstName)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
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
}
