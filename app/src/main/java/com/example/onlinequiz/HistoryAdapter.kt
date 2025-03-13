package com.example.onlinequiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyList: List<QuizHistoryModel>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        holder.quizName.text = history.quizName
//        holder.attemptNumber.text = "Attempt: ${history.attemptNumber}" // attempt number
        holder.scorePercentage.text = "Score: ${history.scorePercentage}%"
        holder.correctAnswers.text = "Correct: ${history.correctAnswers}"
        holder.wrongAnswers.text = "Wrong: ${history.wrongAnswers}"
    }

    override fun getItemCount(): Int = historyList.size
}
