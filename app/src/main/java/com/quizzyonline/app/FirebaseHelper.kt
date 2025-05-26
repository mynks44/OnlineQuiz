package com.quizzyonline.app

import com.quizzyonline.app.models.IncorrectQuestionModel
import com.quizzyonline.app.models.QuizHistoryModel
import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {

    fun saveQuizHistory(userId: String, quizHistory: QuizHistoryModel) {
        val database = FirebaseDatabase.getInstance().reference
        val historyRef = database.child("QuizHistory").child(userId).child("attempts").child(quizHistory.attemptNumber.toString())

        val historyData = mapOf(
            "quizName" to quizHistory.quizName,
            "scorePercentage" to quizHistory.scorePercentage,
            "correctAnswers" to quizHistory.correctAnswers,
            "wrongAnswers" to quizHistory.wrongAnswers,
            "attemptNumber" to quizHistory.attemptNumber,
            "incorrectQuestions" to quizHistory.incorrectQuestions.map {
                mapOf(
                    "question" to it.question,
                    "yourAnswer" to it.yourAnswer,
                    "correctAnswer" to it.correctAnswer
                )
            }
        )

        historyRef.setValue(historyData)
            .addOnSuccessListener { println("✅ Quiz history saved successfully!") }
            .addOnFailureListener { e -> println("❌ Error saving quiz history: ${e.message}") }
    }

    fun fetchQuizHistory(userId: String, callback: (List<QuizHistoryModel>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val historyRef = database.child("QuizHistory").child(userId).child("attempts")

        historyRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val historyList = mutableListOf<QuizHistoryModel>()

                for (attemptSnapshot in snapshot.children) {
                    val quizName = attemptSnapshot.child("quizName").getValue(String::class.java) ?: ""
                    val scorePercentage = attemptSnapshot.child("scorePercentage").getValue(Int::class.java) ?: 0
                    val correctAnswers = attemptSnapshot.child("correctAnswers").getValue(Int::class.java) ?: 0
                    val wrongAnswers = attemptSnapshot.child("wrongAnswers").getValue(Int::class.java) ?: 0
                    val attemptNumber = attemptSnapshot.child("attemptNumber").getValue(Long::class.java) ?: 0

                    val incorrectQuestions = mutableListOf<IncorrectQuestionModel>()
                    for (incorrectSnapshot in attemptSnapshot.child("incorrectQuestions").children) {
                        val question = incorrectSnapshot.child("question").getValue(String::class.java) ?: ""
                        val yourAnswer = incorrectSnapshot.child("yourAnswer").getValue(String::class.java) ?: ""
                        val correctAnswer = incorrectSnapshot.child("correctAnswer").getValue(String::class.java) ?: ""

                        incorrectQuestions.add(IncorrectQuestionModel(question, yourAnswer, correctAnswer))
                    }

                    val history = QuizHistoryModel(
                        userId = userId,
                        quizName = quizName,
                        scorePercentage = scorePercentage,
                        correctAnswers = correctAnswers,
                        wrongAnswers = wrongAnswers,
                        attemptNumber = attemptNumber,
                        incorrectQuestions = incorrectQuestions
                    )

                    historyList.add(history)
                }

                callback(historyList)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                println("❌ Error fetching quiz history: ${error.message}")
            }
        })
    }
}
