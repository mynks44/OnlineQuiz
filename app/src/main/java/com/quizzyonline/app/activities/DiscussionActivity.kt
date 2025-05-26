package com.quizzyonline.app.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quizzyonline.app.adapters.DiscussionAdapter
import com.example.onlinequiz.databinding.ActivityDiscussionBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.quizzyonline.app.models.DiscussionPost
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

        loadDiscussions()

        // Disable buttons until name is ready
        binding.btnPost.isEnabled = false
        binding.btnImage.isEnabled = false

        fetchUserName {
            Log.d("DEBUG_USER", "👤 Final userName to use: $userName")
            binding.btnPost.isEnabled = true
            binding.btnImage.isEnabled = true

            binding.btnPost.setOnClickListener {
                Log.d("DEBUG_USER", "📤 Posting message as: $userName")
                postDiscussion(userName) // ✅ Pass name directly
            }


            binding.btnImage.setOnClickListener {
                pickImage()
            }
        }
    }


    private fun fetchUserName(onFetched: () -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("DEBUG_USER", "❌ No logged-in user!")
            onFetched()
            return
        }

        db.collection("Users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("firstName")
                    Log.d("DEBUG_USER", "✅ Fetched first name from Firestore: $name")
                    userName = name ?: "Student"
                } else {
                    Log.e("DEBUG_USER", "❌ User document does not exist")
                }
                onFetched()
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG_USER", "❌ Failed to fetch user name: ${e.message}")
                onFetched()
            }
    }



    private fun postDiscussion(name: String) {
        Log.d("POST_DEBUG", "Posting as: $name")
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
            uploadImage(postId, postText, name)
        } else {
            savePostToFirestore(postId, postText, null, name)
        }
    }

    private fun uploadImage(postId: String, postText: String, name: String) {
        val filePath = "discussion_images/$subjectName/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(filePath)

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        savePostToFirestore(postId, postText, downloadUri.toString(), name)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                    isPosting = false
                }
        }
    }

    private fun savePostToFirestore(postId: String, postText: String?, imageUrl: String?, name: String) {
        val post = DiscussionPost(
            postId = postId,
            postText = postText,
            imageUrl = imageUrl,
            studentName = name,  // ✅ use passed-in name
            timestamp = Timestamp.now(),
            subjectName = subjectName
        )

        db.collection("discussions").document(subjectName).collection("messages").document(postId)
            .set(post)
            .addOnSuccessListener {
                binding.etPost.text.clear()
                imageUri = null
                isPosting = false
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
                    Toast.makeText(this, "Error loading messages: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DiscussionActivity", "Firestorm error: ", error)

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
