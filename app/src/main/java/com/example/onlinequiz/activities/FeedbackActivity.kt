package com.example.onlinequiz.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.R
import com.example.onlinequiz.databinding.ActivityFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseFeedback: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseFeedback = FirebaseDatabase.getInstance().reference.child("Feedback")

        binding.btnSubmit.setOnClickListener {
            submitFeedback()
        }
    }

    private fun submitFeedback() {
        val userEmail = auth.currentUser?.email // Get logged-in user's email
        val feedbackText = binding.etFeedback.text.toString().trim()

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show()
            return
        }

        val feedbackId = databaseFeedback.push().key

        val feedbackData = hashMapOf(
            "email" to (userEmail ?: "Anonymous"), // Store email with feedback
            "feedback" to feedbackText
        )

        feedbackId?.let {
            databaseFeedback.child(it).setValue(feedbackData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()
                        binding.etFeedback.text.clear()
                    } else {
                        Toast.makeText(this, "Failed to submit feedback: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}
