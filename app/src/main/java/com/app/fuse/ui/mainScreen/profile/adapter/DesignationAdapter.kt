package com.app.fuse.ui.homescreen.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendsConnectionList

class DesignationAdapter:RecyclerView.Adapter<DesignationAdapter.ViewHolder>() {

    inner class ViewHolder(v: View):RecyclerView.ViewHolder(v)

    private val differCallBack = object : DiffUtil.ItemCallback<FriendsConnectionList>() {
        override fun areItemsTheSame(oldItem: FriendsConnectionList, newItem: FriendsConnectionList): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FriendsConnectionList,
            newItem: FriendsConnectionList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesignationAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_designation,parent,false))
    }

    override fun onBindViewHolder(holder: DesignationAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 3
}