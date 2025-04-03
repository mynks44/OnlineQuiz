package com.example.onlinequiz.activities

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlinequiz.adapters.DiscussionAdapter
import com.example.onlinequiz.databinding.ActivityDiscussionBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.example.onlinequiz.models.DiscussionPost
import java.util.*

class DiscussionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiscussionBinding
    private lateinit var discussionAdapter: DiscussionAdapter
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var subjectName: String = "General"
    private var imageUri: Uri? = null
    private var isPosting = false
    private var discussionList = mutableListOf<DiscussionPost>()
    private val auth = FirebaseAuth.getInstance()
    private var userName: String = "Student" // Default name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscussionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subjectName = intent.getStringExtra("subject_name") ?: "General"
        title = "$subjectName Discussion Room"

        discussionAdapter = DiscussionAdapter(this)
        binding.rvDiscussions.layoutManager = LinearLayoutManager(this)
        binding.rvDiscussions.adapter = discussionAdapter

        fetchUserName() // Fetch logged-in user's name
        loadDiscussions()

        binding.btnPost.setOnClickListener { postDiscussion() }
        binding.btnImage.setOnClickListener { pickImage() }
    }

    private fun fetchUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("firstName") ?: "Student"
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch user name", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun postDiscussion() {
        if (isPosting) return
        isPosting = true

        val postText = binding.etPost.text.toString().trim()

        if (postText.isEmpty() && imageUri == null) {
            Toast.makeText(this, "Enter a message or select an image", Toast.LENGTH_SHORT).show()
            isPosting = false
            return
        }

        val postId = db.collection("discussions").document().id

        if (imageUri != null) {
            uploadImage(postId, postText)
        } else {
            savePostToFirestore(postId, postText, null)
        }
    }

    private fun uploadImage(postId: String, postText: String) {
        val filePath = "discussion_images/$subjectName/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(filePath)

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        savePostToFirestore(postId, postText, downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                    isPosting = false
                }
        }
    }

    private fun savePostToFirestore(postId: String, postText: String?, imageUrl: String?) {
        val post = DiscussionPost(
            postId = postId,
            postText = postText,
            imageUrl = imageUrl,
            studentName = userName,
            timestamp = Timestamp.now(),
            subjectName = subjectName
        )

        db.collection("discussions").document(subjectName).collection("messages").document(postId)
            .set(post)
            .addOnSuccessListener {
                binding.etPost.text.clear()
                imageUri = null
                isPosting = false

                discussionList.add(post)
                discussionAdapter.submitList(discussionList.toList())

                binding.rvDiscussions.scrollToPosition(discussionList.size - 1)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to post", Toast.LENGTH_SHORT).show()
                isPosting = false
            }
    }

    private fun loadDiscussions() {
        db.collection("discussions").document(subjectName).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val updatedList = mutableListOf<DiscussionPost>()
                querySnapshot?.forEach { document ->
                    document.toObject(DiscussionPost::class.java)?.let {
                        updatedList.add(it)
                    }
                }

                discussionList = updatedList
                discussionAdapter.submitList(updatedList.toList())

                if (updatedList.isNotEmpty()) {
                    binding.rvDiscussions.scrollToPosition(updatedList.size - 1)
                }
            }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }
}
