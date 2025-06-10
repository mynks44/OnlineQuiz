package com.quizzyonline.app.activities

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlinequiz.databinding.ActivityBattleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.quizzyonline.app.models.ChatMessage
import com.quizzyonline.app.models.QuestionModel
import com.quizzyonline.app.adapters.ChatAdapter

class BattleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBattleBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var roomId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()

    private var currentIndex = 0
    private var questionList = listOf<QuestionModel>()
    private var myScore = 0
    private var opponentScore = 0
    private var selectedOption = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBattleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomId = intent.getStringExtra("roomId") ?: return
        dbRef = FirebaseDatabase.getInstance().getReference("quiz_battles").child(roomId)
        auth = FirebaseAuth.getInstance()

        chatAdapter = ChatAdapter(chatList)
        binding.chatRecycler.layoutManager = LinearLayoutManager(this)
        binding.chatRecycler.adapter = chatAdapter

        listenToChat()

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val msg = ChatMessage(auth.uid ?: "", text, System.currentTimeMillis())
                dbRef.child("chat").push().setValue(msg)
                binding.etMessage.setText("")
            }
        }

        loadQuestions()
    }

    private fun loadQuestions() {
        // Simulated static list for now (replace with Firebase if needed)
        questionList = listOf(
            QuestionModel("What is 2 + 2?", listOf("2", "3", "4", "5"), 2),
            QuestionModel("Capital of France?", listOf("London", "Berlin", "Paris", "Madrid"), 2)
        )
        displayQuestion()
    }

    private fun displayQuestion() {
        val q = questionList[currentIndex]
        binding.tvQuestion.text = q.question
        binding.tvQuestionCount.text = "Question ${currentIndex + 1}/${questionList.size}"
        binding.tvMyScore.text = "Your Score: $myScore"
        binding.tvOpponentScore.text = "Opponent: $opponentScore"

        q.options.forEachIndexed { i, option ->
            (binding.radioGroup.getChildAt(i) as? RadioButton)?.text = option
        }

        selectedOption = -1
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedOption = binding.radioGroup.indexOfChild(findViewById(checkedId))
        }

        binding.btnSubmit.setOnClickListener {
            checkAnswer()
        }
    }

    private fun checkAnswer() {
        val correct = questionList[currentIndex].answer
        if (selectedOption == correct) myScore++
        currentIndex++
        if (currentIndex < questionList.size) {
            displayQuestion()
        } else {
            Toast.makeText(this, "Battle finished!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun listenToChat() {
        dbRef.child("chat").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                snapshot.getValue(ChatMessage::class.java)?.let {
                    chatList.add(it)
                    chatAdapter.notifyItemInserted(chatList.size - 1)
                    binding.chatRecycler.scrollToPosition(chatList.size - 1)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, prev: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, prev: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }
}
