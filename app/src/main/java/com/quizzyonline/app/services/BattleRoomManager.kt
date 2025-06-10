package com.quizzyonline.app.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

object BattleRoomManager {

    private val db = FirebaseDatabase.getInstance().getReference("quiz_battles")
    private val questionPoolRef = FirebaseDatabase.getInstance().getReference("Questions")

    fun createRoom(onRoomCreated: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return
        val roomId = db.push().key ?: UUID.randomUUID().toString()
        val roomRef = db.child(roomId)

        val playerData = mapOf("uid" to user.uid, "name" to (user.displayName ?: "Player"), "score" to 0)
        val roomData = mapOf(
            "player1" to playerData,
            "status" to "waiting",
            "currentQuestion" to 0
        )

        roomRef.setValue(roomData).addOnSuccessListener {
            preloadQuestionsIntoBattle(roomId)
            onRoomCreated(roomId)
        }
    }

    private fun preloadQuestionsIntoBattle(roomId: String, limit: Int = 5) {
        questionPoolRef.get().addOnSuccessListener { snapshot ->
            val shuffled = snapshot.children.shuffled().take(limit)
            val questionList = mutableListOf<Map<String, Any>>()

            shuffled.forEach { snap ->
                val q = snap.value as? Map<String, Any>
                q?.let { questionList.add(it) }
            }

            db.child(roomId).child("questions").setValue(questionList)
        }
    }

    fun joinRoom(roomId: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val playerData = mapOf("uid" to user.uid, "name" to (user.displayName ?: "Player"), "score" to 0)
        db.child(roomId).child("player2").setValue(playerData)
        db.child(roomId).child("status").setValue("in_progress")
    }
}
