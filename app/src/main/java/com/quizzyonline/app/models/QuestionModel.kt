package com.quizzyonline.app.models

data class QuestionModel(
    val text: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val answer: Int = 0,
    val question: String = "",
)
