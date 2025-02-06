package com.example.onlinequiz

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.onlinequiz.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: QuizListAdapter
    lateinit var quizModelList: MutableList<QuizModel>
    lateinit var filteredQuizList: MutableList<QuizModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        filteredQuizList = mutableListOf()

        setupSearchView() //searchbar

        getDataFromFirebase()
    }

    private fun setupRecyclerView(){
        binding.progressBar.visibility = View.GONE
        adapter = QuizListAdapter(filteredQuizList)  // Use filtered list
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterList(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterList(it)
                }
                return true
            }
        })
    }

    private fun filterList(query: String) {
        filteredQuizList.clear()
        if (query.isEmpty()) {
            filteredQuizList.addAll(quizModelList)
        } else {
            for (quiz in quizModelList) {
                if (quiz.title.contains(query, ignoreCase = true)) {
                    filteredQuizList.add(quiz)
                }
            }
        }
        adapter.updateList(filteredQuizList)
    }

    private fun getDataFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().reference
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            quizModelList.add(quizModel)
                        }
                    }
                }
                filteredQuizList.addAll(quizModelList)
                setupRecyclerView()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
    }
}
