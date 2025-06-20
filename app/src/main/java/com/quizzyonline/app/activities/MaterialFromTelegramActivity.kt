package com.quizzyonline.app.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlinequiz.databinding.ActivityMaterialFromTelegramBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.quizzyonline.app.adapters.TelegramMaterialAdapter
import com.quizzyonline.app.models.TelegramMaterialModel

class MaterialFromTelegramActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaterialFromTelegramBinding
    private val materialList = mutableListOf<TelegramMaterialModel>()
    private lateinit var adapter: TelegramMaterialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialFromTelegramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TelegramMaterialAdapter(materialList.toMutableList(),
            onJoin = { item -> openTelegramLink(item.joinLink) },
            onOpen = { item -> openTelegramLink(item.link) })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadMaterials()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText.orEmpty())
                return true
            }
        })
    }

    private fun loadMaterials() {
        FirebaseFirestore.getInstance().collection("telegram_materials")
            .orderBy("title")
            .get()
            .addOnSuccessListener { snapshot ->
                materialList.clear()
                for (doc in snapshot.documents) {
                    doc.toObject(TelegramMaterialModel::class.java)?.let { materialList.add(it) }
                }
                adapter.updateList(materialList)
            }
    }

    private fun filterList(query: String) {
        val filtered = materialList.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.type.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }

    private fun isTelegramInstalled(): Boolean {
        val packageManager = packageManager
        val telegramPackages = listOf(
            "org.telegram.messenger",
            "org.telegram.messenger.web",
            "org.thunderdog.challegram"
        )

        for (pkg in telegramPackages) {
            try {
                packageManager.getPackageInfo(pkg, 0)
                return true
            } catch (e: Exception) {
                // Package not found, try next
            }
        }
        return false
    }

    private fun openTelegramLink(link: String?) {
        if (link.isNullOrBlank()) {
            Toast.makeText(this, "Invalid or missing Telegram link.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            intent.setPackage("org.telegram.messenger")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(fallbackIntent)
            } catch (ex: Exception) {
                showInstallTelegramDialog()
            }
        }
    }

    private fun showInstallTelegramDialog() {
        AlertDialog.Builder(this)
            .setTitle("Telegram Not Installed")
            .setMessage("Please install the Telegram app to view this content.")
            .setPositiveButton("Go to Play Store") { _, _ ->
                val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")
                }
                startActivity(playStoreIntent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}