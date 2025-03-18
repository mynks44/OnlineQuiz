package com.example.onlinequiz.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.R
import com.example.onlinequiz.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    private var isLoginMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().getReference("Users")

        binding.btnSwitch.setOnClickListener {
            toggleLoginSignup()
        }

        binding.btnSignUp.setOnClickListener {
            if (isLoginMode) {
                loginUser()
            } else {
                registerUser()
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            resetPassword()
        }
    }

    private fun toggleLoginSignup() {
        isLoginMode = !isLoginMode

        if (isLoginMode) {
            binding.etFirstName.visibility = View.GONE
            binding.etLastName.visibility = View.GONE
            binding.btnSignUp.text = "Login"
            binding.btnSwitch.text = "Don't have an account? Sign Up"
        } else {
            binding.etFirstName.visibility = View.VISIBLE
            binding.etLastName.visibility = View.VISIBLE
            binding.btnSignUp.text = "Sign Up"
            binding.btnSwitch.text = "Already have an account? Login"
        }
    }

    private fun registerUser() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email
                    )

                    userId?.let {
                        database.child(it).setValue(user).addOnCompleteListener {
                            Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                            goToMainActivity(firstName)
                        }
                    }
                } else {
                    Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        database.child(it).get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                val firstName = snapshot.child("firstName").value.toString()
                                saveLoginDetails(email, password, firstName)
                                goToMainActivity(firstName)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveLoginDetails(email: String, password: String, firstName: String) {
        val editor = sharedPreferences.edit()
        if (binding.rememberMeCheckBox.isChecked) {
            editor.putString("email", email)
            editor.putString("password", password)
            editor.putBoolean("rememberMe", true)
        } else {
            editor.clear()
        }
        editor.putString("firstName", firstName)
        editor.apply()
    }

    private fun resetPassword() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun goToMainActivity(firstName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("firstName", firstName)
        startActivity(intent)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}
