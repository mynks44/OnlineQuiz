package com.example.onlinequiz

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().getReference("Users")

        binding.btnSignIn.visibility = View.GONE
        binding.btnSignUp.visibility = View.VISIBLE
        binding.btnLogin.text = "Already have an account? Login"

        binding.btnLogin.setOnClickListener {
            if (binding.btnSignUp.visibility == View.VISIBLE) {
                binding.btnSignUp.visibility = View.GONE
                binding.btnSignIn.visibility = View.VISIBLE
                binding.btnLogin.text = "Don't have an account? Sign Up"
            } else {
                binding.btnSignIn.visibility = View.GONE
                binding.btnSignUp.visibility = View.VISIBLE
                binding.btnLogin.text = "Already have an account? Login"
            }
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val user = hashMapOf(
                            "email" to email,
                            "firstName" to binding.etFirstName.text.toString(),
                            "lastName" to binding.etLastName.text.toString()
                        )

                        userId?.let {
                            database.child(it).setValue(user).addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to store user data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.btnSignIn.setOnClickListener {
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
                        userId?.let {
                            database.child(it).get().addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    val firstName = snapshot.child("firstName").getValue() as? String
                                    val editor = sharedPreferences.edit()

                                    if (binding.rememberMeCheckBox.isChecked) {
                                        editor.putString("email", email)
                                        editor.putString("password", password)
                                        editor.putBoolean("rememberMe", true)
                                    } else {
                                        editor.clear()
                                    }
                                    editor.apply()

                                    editor.putString("firstName", firstName)
                                    editor.apply()

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
