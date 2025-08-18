package com.quizzyonline.app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.R
import com.example.onlinequiz.databinding.ActivityFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

//        binding.btnSubmit.setOnClickListener {
//            submitToFirebase()
//        }

        binding.btnSendEmail.setOnClickListener {
            showEmailDialog()
        }
    }

    private fun submitToFirebase() {
        val feedbackText = binding.etFeedback.text.toString().trim()
        val userEmail = auth.currentUser?.email ?: "Anonymous"

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show()
            return
        }

        val feedbackRef = FirebaseDatabase.getInstance().reference.child("Feedback")
        val feedbackId = feedbackRef.push().key ?: return
        val feedbackData = mapOf(
            "email" to userEmail,
            "feedback" to feedbackText
        )

        feedbackRef.child(feedbackId).setValue(feedbackData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Feedback submitted!", Toast.LENGTH_SHORT).show()
                    binding.etFeedback.text.clear()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showEmailDialog() {
        val feedbackText = binding.etFeedback.text.toString().trim()
        val userEmail = auth.currentUser?.email ?: "Anonymous"

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Send Email")
            .setMessage("Do you want to send this feedback to quizzyonlinequiz@gmail.com?")
            .setPositiveButton("Send") { _, _ ->
                sendFeedbackEmail(userEmail, feedbackText)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendFeedbackEmail(email: String, feedback: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:quizzyonlinequiz@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "User Feedback from $email")
            putExtra(Intent.EXTRA_TEXT, feedback)
        }

        try {
            startActivity(Intent.createChooser(intent, "Send Feedback Email"))
        } catch (e: Exception) {
            Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
