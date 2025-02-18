package com.example.onlinequiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.aboutUsText.text = "Welcome to Quiz App!\n\nWe provide interactive quizzes to help students learn in a fun way."
    }
}
