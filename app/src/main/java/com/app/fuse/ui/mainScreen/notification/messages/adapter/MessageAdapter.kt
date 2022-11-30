package com.app.fuse.ui.homescreen.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.databinding.ListMessagesBinding
import com.app.fuse.ui.mainScreen.notification.messages.model.ConversationList
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    lateinit var context: Context

    inner class ViewHolder(val binding: ListMessagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ConversationList) {
            with(binding) {
                Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch())
                    .load(data.profile).into(userImage)
                userName.text = data.username
                lastMessage.text = data.message
                if (data.un_read <= 0 || data.un_read == null) {
                    messageCount.visibility = View.GONE
                } else {
                    messageCount.text = data.un_read.toString()
                }

                lastMessageTime.text = data.time

                messageListLayout.setOnClickListener {
                    messageCount.visibility = View.GONE
                    onItemClickListener?.let { it(data) }
                }
            }
        }
    }


    val differCallBack = object : DiffUtil.ItemCallback<ConversationList>() {
        override fun areItemsTheSame(
            oldItem: ConversationList,
            newItem: ConversationList
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ConversationList,
            newItem: ConversationList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }


    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ListMessagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private var onItemClickListener: ((ConversationList) -> Unit)? = null

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {

        holder.bind(differ.currentList[position])
    }

    fun setOnItemClickListener(listener: (ConversationList) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int = differ.currentList.size
}