package com.example.onlinequiz

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<QuizHistoryModel>()

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("History")

        fetchQuizHistory()
    }

    private fun fetchQuizHistory() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        database.child(userId).orderByChild("attemptNumber").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val historyItem = data.getValue(QuizHistoryModel::class.java)
                        if (historyItem != null) {
                            historyList.add(historyItem)
                        }
                    }
                    historyList.sortByDescending { it.attemptNumber }
                    historyAdapter = HistoryAdapter(historyList)
                    recyclerView.adapter = historyAdapter
                } else {
                    Toast.makeText(this@HistoryActivity, "No quiz history found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HistoryActivity", "Error fetching history: ${error.message}")
                Toast.makeText(this@HistoryActivity, "Failed to fetch history!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
