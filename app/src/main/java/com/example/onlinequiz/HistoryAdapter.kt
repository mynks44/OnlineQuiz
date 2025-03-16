package com.example.onlinequiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]

        // Use safe call `?.` and Elvis operator `?:` to avoid crashes
        holder.quizName.text = history.quizName ?: "Unknown Quiz"
        holder.attemptNumber.text = "Attempt: ${history.attemptNumber ?: 1}" // Default attempt 1
        holder.scorePercentage.text = "Score: ${history.scorePercentage ?: 0}%"
        holder.correctAnswers.text = "Correct: ${history.correctAnswers ?: 0}"
        holder.wrongAnswers.text = "Wrong: ${history.wrongAnswers ?: 0}"
    }

    override fun getItemCount(): Int = historyList.size
}
