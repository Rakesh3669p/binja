package com.app.fuse.ui.homescreen.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.ui.homescreen.profile.model.RequestReceivedUserData
import kotlinx.android.synthetic.main.list_request_received.view.*

class RequestReceivedAdapter:RecyclerView.Adapter<RequestReceivedAdapter.ViewHolder>() {
    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v)
    private val differCallBack = object : DiffUtil.ItemCallback<RequestReceivedUserData>() {
        override fun areItemsTheSame(oldItem: RequestReceivedUserData, newItem: RequestReceivedUserData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RequestReceivedUserData,
            newItem: RequestReceivedUserData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestReceivedAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_request_received,parent,false))

    }
    private var onAcceptRequestClickListner: ((Int) -> Unit)? = null
    private var onRejectRequestClickListner: ((Int) -> Unit)? = null

    override fun onBindViewHolder(holder: RequestReceivedAdapter.ViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.itemView.apply {
            requestReceivedUserName.text = model.request_sender_user.username
            requestReceivedUserAge.text ="${context.getString(R.string.age_lbl)} :${model.request_sender_user.username}"

            requestReceivedAcceptCV.setOnClickListener {
                onAcceptRequestClickListner?.let { it(model.id!!) }
            }
            requestReceivedRejectCV.setOnClickListener {
                onRejectRequestClickListner?.let { it(model.id!!) }
            }

        }
    }

    fun setOnAcceptRequestListener(listener: (Int) -> Unit) {
        onAcceptRequestClickListner = listener
    }

    fun setOnRejectRequestListener(listener: (Int) -> Unit) {
        onRejectRequestClickListner = listener
    }


    override fun getItemCount(): Int =differ.currentList.size
}