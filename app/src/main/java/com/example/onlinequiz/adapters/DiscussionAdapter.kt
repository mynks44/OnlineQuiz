package com.example.onlinequiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinequiz.R
import com.example.onlinequiz.models.DiscussionPost

class DiscussionAdapter(private val context: Context) :
    ListAdapter<DiscussionPost, DiscussionAdapter.ViewHolder>(DiscussionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_discussion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val discussion = getItem(position)

        holder.questionTextView.text = discussion.postText ?: "No text"
        holder.studentNameTextView.text = discussion.studentName

        if (!discussion.imageUrl.isNullOrEmpty()) {
            holder.imageView.visibility = View.VISIBLE
            Glide.with(context)
                .load(discussion.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
        }

        if (discussion.isPermanent) {
            holder.permanentTextView.visibility = View.VISIBLE
        } else {
            holder.permanentTextView.visibility = View.GONE
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
        val studentNameTextView: TextView = itemView.findViewById(R.id.studentNameTextView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val permanentTextView: TextView = itemView.findViewById(R.id.permanentTextView)
    }

    class DiscussionDiffCallback : DiffUtil.ItemCallback<DiscussionPost>() {
        override fun areItemsTheSame(oldItem: DiscussionPost, newItem: DiscussionPost): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: DiscussionPost, newItem: DiscussionPost): Boolean {
            return oldItem == newItem
        }
    }
}
