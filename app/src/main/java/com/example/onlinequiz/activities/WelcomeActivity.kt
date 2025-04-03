package com.example.onlinequiz.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.R
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val displayName = user.displayName
            if (displayName.isNullOrEmpty()) {
                Toast.makeText(this, "⚠️ No username found!", Toast.LENGTH_LONG).show()
                Log.e("WelcomeActivity", "⚠️ No username found in FirebaseAuth.")
            } else {
                Log.d("WelcomeActivity", "✅ Username: $displayName")
            }
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
