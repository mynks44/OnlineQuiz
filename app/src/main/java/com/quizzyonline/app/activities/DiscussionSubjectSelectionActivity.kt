package com.quizzyonline.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.R

class DiscussionSubjectSelectionActivity : AppCompatActivity() {

    private val subjects = listOf(
        "Mathematics", "Physics", "Chemistry", "Biology",
        "Computer Science", "History", "Geography",
        "English", "Economics", "Political Science", "Business Studies"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion_subject_selection)

        val listView: ListView = findViewById(R.id.subjectListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, subjects)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedSubject = subjects[position]
            val intent = Intent(this, DiscussionActivity::class.java)
            intent.putExtra("subject_name", selectedSubject)
            startActivity(intent)
        }
    }
}
