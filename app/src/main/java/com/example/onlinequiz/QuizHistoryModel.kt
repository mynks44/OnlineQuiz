package com.example.onlinequiz

data class QuizHistoryModel(
    val userId: String = "",
    val quizName: String = "",
    val scorePercentage: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val attemptNumber: Long = 0,
    val incorrectQuestions: List<IncorrectQuestionModel> = emptyList()

)

data class IncorrectQuestionModel(
    val question: String = "",
    val yourAnswer: String = "",
    val correctAnswer: String = ""
)
