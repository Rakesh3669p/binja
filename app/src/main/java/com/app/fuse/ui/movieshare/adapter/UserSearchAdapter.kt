package com.app.fuse.ui.movieshare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.databinding.ListSearchUsersMovieShareBinding
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.app.fuse.utils.common.showToast
import com.bumptech.glide.Glide
import com.app.fuse.ui.mainScreen.search.model.SearchList

class UserSearchAdapter : RecyclerView.Adapter< UserSearchAdapter .ViewHolder>() {
    lateinit var context: Context
    inner class ViewHolder(val binding: ListSearchUsersMovieShareBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data:SearchList){
            with(binding){
                Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch()).load("${data.profile}").into(searchUserImage)
                searchUserName.text = data.username
                shareMovieShare.setOnClickListener {
                    context.showToast("Tapped..")
                }
            }
        }
    }


    private val diffCallBack = object : DiffUtil.ItemCallback<SearchList>() {
        override fun areItemsTheSame(oldItem: SearchList, newItem: SearchList): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(
            oldItem: SearchList,
            newItem: SearchList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserSearchAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ListSearchUsersMovieShareBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: UserSearchAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
}