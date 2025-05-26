package com.quizzyonline.app

data class QuizModel(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val time: String = "",
    val locked: Boolean = false,
    val questionList: List<QuestionModel> = emptyList()
)

data class QuestionModel(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correct: String = "",
    var userAnswer: String? = null,
    val explanation: String = ""
)
