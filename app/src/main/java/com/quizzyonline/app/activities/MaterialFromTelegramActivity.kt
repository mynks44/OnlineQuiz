package com.quizzyonline.app.activities


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlinequiz.databinding.ActivityMaterialFromTelegramBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.quizzyonline.app.adapters.TelegramMaterialAdapter
import com.quizzyonline.app.models.TelegramMaterialModel

class MaterialFromTelegramActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaterialFromTelegramBinding
    private val materialList = mutableListOf<TelegramMaterialModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialFromTelegramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        FirebaseFirestore.getInstance().collection("telegram_materials")
            .orderBy("title")
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.toObject(TelegramMaterialModel::class.java)?.let { materialList.add(it) }
                }

                val adapter = TelegramMaterialAdapter(materialList) { item ->
                    if (item.joinedRequired) {
                        showJoinDialog(item.link)
                    } else {
                        openTelegramLink(item.link)
                    }
                }

                binding.recyclerView.adapter = adapter
            }
    }

    private fun openTelegramLink(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            intent.setPackage("org.telegram.messenger")
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }
    }

    private fun showJoinDialog(link: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Join Telegram Channel")
            .setMessage("You need to join our private Telegram channel to access this content.")
            .setPositiveButton("Join Now") { _, _ ->
                openTelegramLink("https://t.me/yourchannel") // your actual channel link
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
}
