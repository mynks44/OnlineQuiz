package com.example.onlinequiz

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object QuizHistoryManager {
    private const val PREFS_NAME = "quiz_history"
    private const val HISTORY_KEY = "history"

    fun saveQuizResult(context: Context, quizName: String, score: Int, questions: List<QuestionModel>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Load existing history
        val historyList = getHistory(context).toMutableList()

        // Build history entry
        val correctAnswers = questions.count { it.userAnswer == it.correct }
        val incorrectAnswers = questions.size - correctAnswers
        val percentage = (score.toFloat() / questions.size * 100).toInt()

        val historyEntry = "Quiz: $quizName | Score: $score/${questions.size} ($percentage%)\nCorrect: $correctAnswers, Incorrect: $incorrectAnswers"

        // Add new entry
        historyList.add(0, historyEntry) // Newest first

        // Save back to shared preferences
        val gson = Gson()
        editor.putString(HISTORY_KEY, gson.toJson(historyList))
        editor.apply()
    }

    fun getHistory(context: Context): List<String> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(HISTORY_KEY, "[]")
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}
