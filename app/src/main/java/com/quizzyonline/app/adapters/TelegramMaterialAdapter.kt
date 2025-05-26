package com.quizzyonline.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinequiz.R
import com.quizzyonline.app.models.TelegramMaterialModel
class TelegramMaterialAdapter(
    private val list: List<TelegramMaterialModel>,
    private val onOpen: (TelegramMaterialModel) -> Unit
) : RecyclerView.Adapter<TelegramMaterialAdapter.ViewHolder>() {  // ✅ IMPORTANT!

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val type: TextView = view.findViewById(R.id.tvType)
        val open: Button = view.findViewById(R.id.btnOpen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_telegram_material, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.type.text = item.type
        holder.open.setOnClickListener { onOpen(item) }
    }

    override fun getItemCount(): Int = list.size
}
