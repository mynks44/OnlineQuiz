package com.quizzyonline.app.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.quizzyonline.app.services.BattleRoomManager

class BattleLobbyActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var currentUserId: String
    private var hasJoinedRoom = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("quiz_battles")
        currentUserId = FirebaseAuth.getInstance().uid ?: return

        // Try to find an open room
        findOpenRoomOrCreateNew()
    }

    private fun findOpenRoomOrCreateNew() {
        database.get().addOnSuccessListener { snapshot ->
            var joined = false

            for (room in snapshot.children) {
                val status = room.child("status").value.toString()
                if (status == "waiting") {
                    BattleRoomManager.joinRoom(room.key!!)
                    navigateToBattle(room.key!!)
                    joined = true
                    break
                }
            }

            if (!joined) {
                BattleRoomManager.createRoom { roomId ->
                    waitForSecondPlayer(roomId)
                }
            }
        }
    }

    private fun waitForSecondPlayer(roomId: String) {
        database.child(roomId).child("player2")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && !hasJoinedRoom) {
                        hasJoinedRoom = true
                        navigateToBattle(roomId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun navigateToBattle(roomId: String) {
        val intent = Intent(this, BattleActivity::class.java)
        intent.putExtra("roomId", roomId)
        startActivity(intent)
        finish()
    }
}
