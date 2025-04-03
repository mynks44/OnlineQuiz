package com.example.onlinequiz.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinequiz.QuestionModel
import com.example.onlinequiz.R

class ReviewAdapter(private val questions: List<QuestionModel>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question)
    }

    override fun getItemCount(): Int = questions.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.tvQuestion)
        private val userAnswerText: TextView = itemView.findViewById(R.id.tvUserAnswer)
        private val correctAnswerText: TextView = itemView.findViewById(R.id.tvCorrectAnswer)
        private val explanationText: TextView = itemView.findViewById(R.id.tvExplanation) // New

        @SuppressLint("SetTextI18n")
        fun bind(question: QuestionModel) {
            questionText.text = "Q: ${question.question}"
            userAnswerText.text = "Your Answer: ${question.userAnswer ?: "Not Answered"}"
            correctAnswerText.text = "Correct Answer: ${question.correct}"

            if (question.userAnswer != null && question.userAnswer != question.correct) {
                userAnswerText.setTextColor(itemView.resources.getColor(R.color.red))
                explanationText.visibility = View.VISIBLE
                explanationText.text = "Explanation: ${question.explanation}"
            } else {
                userAnswerText.setTextColor(itemView.resources.getColor(R.color.green))
                explanationText.visibility = View.GONE
            }
        }
    }
}

