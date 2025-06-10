package com.quizzyonline.app.models

data class ChatMessage(
    val sender: String = "",
    val message: String = "",
    val timestamp: Long = 0
)
