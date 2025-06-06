package com.quizzyonline.app.adapters

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinequiz.R
import com.quizzyonline.app.models.TelegramMaterialModel

class TelegramMaterialAdapter(
    private val list: MutableList<TelegramMaterialModel>,
    private val onJoin: (TelegramMaterialModel) -> Unit,
    private val onOpen: (TelegramMaterialModel) -> Unit
) : RecyclerView.Adapter<TelegramMaterialAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val type: TextView = view.findViewById(R.id.tvType)
        val join: Button = view.findViewById(R.id.btnJoin)
        val open: Button = view.findViewById(R.id.btnOpen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_telegram_material, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        Log.d("ADAPTER_DEBUG", "Binding: ${item.title}")
        holder.title.text = item.title
        holder.type.text = item.type
        holder.join.setOnClickListener {
            Log.d("JOIN_CLICKED", "Join Link: ${item.joinLink}")
            onJoin(item)
            item.isJoined = true
        }

        holder.join.setOnClickListener {
            onJoin(item)
            item.isJoined = true
        }

        holder.open.setOnClickListener {
            if (item.isJoined) {
                onOpen(item)
            } else {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Join Required")
                    .setMessage("Have you already joined the Telegram channel?")
                    .setPositiveButton("Yes, I Joined") { _, _ ->
                        item.isJoined = true
                        onOpen(item)
                    }
                    .setNegativeButton("Join Now") { _, _ ->
                        onJoin(item)
                    }
                    .setNeutralButton("Cancel", null)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = list.size
    fun updateList(newList: List<TelegramMaterialModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}