package com.quizzyonline.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quizzyonline.app.QuizModel
import com.example.onlinequiz.R
import com.quizzyonline.app.adapters.ReviewAdapter
import com.example.onlinequiz.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var quizModel: QuizModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModel = intent.getParcelableExtra("quizData") ?: QuizModel()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        reviewAdapter = ReviewAdapter(quizModel.questionList)
        binding.recyclerView.adapter = reviewAdapter
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}
