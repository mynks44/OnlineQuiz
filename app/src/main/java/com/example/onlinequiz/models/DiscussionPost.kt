package com.example.onlinequiz.models

import com.google.firebase.Timestamp

data class DiscussionPost(
    var postId: String = "",
    var postText: String? = null,
    var studentName: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var isPermanent: Boolean = false,
    var subjectName: String = "",
    var imageUrl: String? = null,
    val explanation: String = ""
) {
    fun getQuestion(): String? {
        return postText
    }

}
