package com.app.fuse.ui.chatmodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.ui.chatmodule.model.ChatDataList
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide
import com.github.nkzawa.utf8.UTF8
import kotlinx.android.synthetic.main.item_chat_date.view.*
import kotlinx.android.synthetic.main.item_received_message.view.*
import kotlinx.android.synthetic.main.item_send_message.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val TYPE_MESSAGE_SENT = 0
    private val TYPE_MESSAGE_RECEIVED = 1
    private val TYPE_HEADER_PROFILE = 4

    private var messageCount = 0
    var lastDate: String = ""
    var currentDate: String = ""
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val simpleDateFormat: DateFormat = SimpleDateFormat("dd-MMM-yyyy")

    lateinit var mRecyclerView: RecyclerView

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)


    private val diffCallBack = object : DiffUtil.ItemCallback<ChatDataList>() {
        override fun areItemsTheSame(oldItem: ChatDataList, newItem: ChatDataList): Boolean {
            return oldItem.message == newItem.message
        }

        override fun areContentsTheSame(
            oldItem: ChatDataList,
            newItem: ChatDataList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatAdapter.ViewHolder {


        return if (viewType == TYPE_MESSAGE_RECEIVED) {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_received_message,
                    parent,
                    false
                )
            )
        } else {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_send_message,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.itemView.apply {
            when (getItemViewType(position)) {
                TYPE_HEADER_PROFILE -> {

                }
                else -> {
                    val date: Date = dateFormat.parse(model.created_at)
                    lastDate = simpleDateFormat.format(date)

                    if (differ.currentList.size - 1 != position) {
                        val previousdate: Date = dateFormat.parse(differ.currentList[position + 1].created_at)
                        val latestdate: Date = dateFormat.parse(model.created_at)

                        lastDate = simpleDateFormat.format(previousdate).toString()
                        currentDate = simpleDateFormat.format(latestdate).toString()

                        if (lastDate != currentDate) {
                            conversationDate.visibility = View.VISIBLE
                            conversationDate.text = currentDate
                        } else {
                            conversationDate.visibility = View.GONE
                        }

                    } else {
                        val latestdate: Date = dateFormat.parse(model.created_at)
                        lastDate = simpleDateFormat.format(latestdate).toString()
                        conversationDate.visibility = View.VISIBLE
                        conversationDate.text = lastDate
                    }

                    if (model.message_postion == "left") {
                        Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch())
                            .load(model.profile).into(cvReceiverPic)
                        tvReceiverMsg.text = UTF8.decode(model.message)
                        tl_ReceiverTime.text = model.time

                    } else {

                        Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch())
                            .load(model.profile).into(cvSenderPic)
                        tvSenderMsg.text = UTF8.decode(model.message)
                        tlSenderTime.text = model.time

                    }
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position].message_postion) {
            "left" -> TYPE_MESSAGE_RECEIVED
            else -> TYPE_MESSAGE_SENT

        }
    }


    override fun getItemCount(): Int = differ.currentList.size

    fun addMessageCount(count: Int) {
        messageCount = count
    }
}