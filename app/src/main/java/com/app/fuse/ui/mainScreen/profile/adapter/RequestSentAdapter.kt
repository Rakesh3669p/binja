package com.app.fuse.ui.homescreen.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.ui.homescreen.profile.model.RequestSentUserData
import kotlinx.android.synthetic.main.list_request_sent.view.*

class RequestSentAdapter:RecyclerView.Adapter<RequestSentAdapter.ViewHolder>() {
    inner class ViewHolder (v:View):RecyclerView.ViewHolder(v)

        private val differCallBack = object : DiffUtil.ItemCallback<RequestSentUserData>() {
        override fun areItemsTheSame(oldItem: RequestSentUserData, newItem: RequestSentUserData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RequestSentUserData,
            newItem: RequestSentUserData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestSentAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_request_sent,parent,false))
    }

    private var onCancelRequestClickListner: ((Int) -> Unit)? = null

    override fun onBindViewHolder(holder: RequestSentAdapter.ViewHolder, position: Int) {
        val model = differ.currentList[position]


        holder.itemView.apply {
            requestSentUserName.text = model.request_receiver_user.username
            requestSentUserName.text = model.request_receiver_user.username
            requestSentUserAge.text ="${context.getString(R.string.age_lbl)} :${model.request_receiver_user.username}"

            requestSentCancelCV.setOnClickListener {
                onCancelRequestClickListner?.let { it(model.id!!) }
            }
        }
    }

    fun setOnonCancelRequestListener(listener: (Int) -> Unit) {
        onCancelRequestClickListner = listener
    }

    override fun getItemCount(): Int = differ.currentList.size
}