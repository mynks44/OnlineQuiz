package com.example.onlinequiz

import android.os.Bundle
import android.view.inputmethod.InputBinding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.onlinequiz.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: QuizListAdapter
    lateinit var quizModelList: MutableList<QuizModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        getDataFromFirebase()

    }

    private fun setupRecyclerView(){
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase(){
        quizModelList.add(QuizModel("1", "Programming", "All the basic programming", "10"))
        quizModelList.add(QuizModel("2", "Computer", "All the computer questions", "20"))
        quizModelList.add(QuizModel("3", "Geography", "boost your geographic knowledge", "30"))
        setupRecyclerView()
    }
}