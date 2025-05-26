package com.quizzyonline.app.models


data class TelegramMaterialModel(
    val title: String = "",
    val subject: String = "",
    val clazz: String = "",
    val type: String = "",
    val link: String = "",
    val joinedRequired: Boolean = true
)
