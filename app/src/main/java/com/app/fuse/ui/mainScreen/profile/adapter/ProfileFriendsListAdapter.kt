package com.app.fuse.ui.mainScreen.profile.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.ListProfileFriendsBinding
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendsConnectionList
import com.app.fuse.utils.common.getPlaceHolder
import com.bumptech.glide.Glide

class ProfileFriendsListAdapter : RecyclerView.Adapter<ProfileFriendsListAdapter.ViewHolder>() {
    lateinit var context: Context

    inner class ViewHolder(val binding: ListProfileFriendsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FriendsConnectionList) {
            with(binding) {
                Glide.with(context).setDefaultRequestOptions(getPlaceHolder())
                    .load(data.friend_user!!.profile)
                    .into(userImage)
                friendListUserName.text = data.friend_user!!.username

                movieMatchesCV.setOnClickListener {
                    onClickListener?.let { it(data.friend_user?.id!!) }

                }
            }

        }
    }


    private val diffCallBack = object : DiffUtil.ItemCallback<FriendsConnectionList>() {
        override fun areItemsTheSame(
            oldItem: FriendsConnectionList,
            newItem: FriendsConnectionList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FriendsConnectionList,
            newItem: FriendsConnectionList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    private var onClickListener: ((Int) -> Unit)? = null

    fun setOnClickListener(listener: (Int) -> Unit) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileFriendsListAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ListProfileFriendsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileFriendsListAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.itemView.apply {
            setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("friendID",differ.currentList[position].friend_user?.id!!)
                findNavController().navigate(R.id.fragmnetMovieMatchedList,bundle)
            }


        }

    }

    override fun getItemCount(): Int = differ.currentList.size
}