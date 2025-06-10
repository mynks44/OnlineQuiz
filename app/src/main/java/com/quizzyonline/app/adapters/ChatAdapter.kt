package com.quizzyonline.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinequiz.R
import com.quizzyonline.app.models.ChatMessage

class ChatAdapter(private val list: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMsg: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.txtMsg.text = list[pos].message
    }

    override fun getItemCount() = list.size
}
