package com.example.onlinequiz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

        @SuppressLint("SetTextI18n")
        fun bind(question: QuestionModel) {
            questionText.text = "Q: ${question.question}"
            userAnswerText.text = "Your Answer: ${question.userAnswer ?: "Not Answered"}"
            correctAnswerText.text = "Correct Answer: ${question.correct}"

            // Highlight wrong answers
            if (question.userAnswer != null && question.userAnswer != question.correct) {
                userAnswerText.setTextColor(itemView.resources.getColor(R.color.red))
            } else {
                userAnswerText.setTextColor(itemView.resources.getColor(R.color.green))
            }
        }
    }
}
