package com.example.onlinequiz.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinequiz.models.QuizHistoryModel
import com.example.onlinequiz.R

class HistoryAdapter(private val historyList: List<QuizHistoryModel>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val attemptNumber: TextView = view.findViewById(R.id.attemptNumber)
        val quizName: TextView = view.findViewById(R.id.quizName)
        val scorePercentage: TextView = view.findViewById(R.id.scorePercentage)
        val correctAnswers: TextView = view.findViewById(R.id.correctAnswers)
        val wrongAnswers: TextView = view.findViewById(R.id.wrongAnswers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.quizName.text = history.quizName
        holder.attemptNumber.text = "Attempt: ${history.attemptNumber}"
        holder.scorePercentage.text = "Score: ${history.scorePercentage}%"
        holder.correctAnswers.text = "Correct: ${history.correctAnswers}"
        holder.wrongAnswers.text = "Wrong: ${history.wrongAnswers}"

        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_fade_in)
        holder.itemView.startAnimation(animation)
    }


    override fun getItemCount(): Int = historyList.size
}


