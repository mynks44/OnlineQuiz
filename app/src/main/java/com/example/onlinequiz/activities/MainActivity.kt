package com.example.onlinequiz.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlinequiz.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.widget.SearchView
import com.example.onlinequiz.adapters.QuizListAdapter
import com.example.onlinequiz.QuizModel
import com.example.onlinequiz.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var adapter: QuizListAdapter
    private lateinit var quizModelList: MutableList<QuizModel>
    private lateinit var filteredQuizList: MutableList<QuizModel>
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.open, R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        auth = FirebaseAuth.getInstance()
        databaseUser = FirebaseDatabase.getInstance().reference.child("Users")
        databaseQuestions = FirebaseDatabase.getInstance().reference.child("questions")

        quizModelList = mutableListOf()
        filteredQuizList = mutableListOf()

        fetchFirstName()
        setupSearchView()
        getDataFromFirebase()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show()
            R.id.nav_about -> startActivity(Intent(this, AboutUsActivity::class.java))
            R.id.nav_feedback -> startActivity(Intent(this, FeedbackActivity::class.java))
            R.id.nav_history -> startActivity(Intent(this, HistoryActivity::class.java))
            R.id.nav_discussions -> {
                // ✅ Open Subject Selection for Discussions
                startActivity(Intent(this, DiscussionSubjectSelectionActivity::class.java))
            }
            R.id.nav_sign_out -> signOutUser()
            R.id.nav_exit -> finishAffinity() // ✅ Close the entire app
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer
        return true
    }

    private fun signOutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.progressBar.visibility = View.GONE
        adapter = QuizListAdapter(filteredQuizList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterList(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterList(it) }
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
            .child("Questions")
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    quizModelList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            quizModelList.add(quizModel)
                        }
                    }
                    filteredQuizList.addAll(quizModelList)
                    setupRecyclerView()
                } else {
                    Log.d("FirebaseData", "No quizzes available in the database.")
                }
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.d("FirebaseData", "Failed to fetch quizzes: ${exception.message}")
                Toast.makeText(this, "Failed to load data.", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchFirstName() {
        val userId = auth.currentUser?.uid ?: return

        databaseUser.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val firstName = snapshot.child("firstName").value.toString()
                val headerView = binding.navigationView.getHeaderView(0)
                val welcomeTextView = headerView.findViewById<TextView>(R.id.navHeaderText)
                welcomeTextView.text = "Welcome, $firstName!"
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch first name", Toast.LENGTH_SHORT).show()
        }
    }
}
