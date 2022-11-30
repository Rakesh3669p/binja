package com.app.fuse.ui.friendsMoviematched.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.databinding.ListFriendsMovieMatchedBinding
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide
import com.app.fuse.ui.friendsMoviematched.model.FriendsMovieMatchedData


class FriendsMovieMatchedAdapter : RecyclerView.Adapter< FriendsMovieMatchedAdapter .ViewHolder>() {
    lateinit var context: Context
    inner class ViewHolder(val binding: ListFriendsMovieMatchedBinding) : RecyclerView.ViewHolder(binding.root){
                fun bind(data: FriendsMovieMatchedData){
            with(binding){
                Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch()).load(data.user.profile).into(userImage)
                userName.text = data.user.username
            }
        }
    }


    private val diffCallBack = object : DiffUtil.ItemCallback<FriendsMovieMatchedData>() {
        override fun areItemsTheSame(oldItem: FriendsMovieMatchedData, newItem: FriendsMovieMatchedData): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(
            oldItem: FriendsMovieMatchedData,
            newItem: FriendsMovieMatchedData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsMovieMatchedAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(ListFriendsMovieMatchedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FriendsMovieMatchedAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])

    }

    override fun getItemCount(): Int = differ.currentList.size
}