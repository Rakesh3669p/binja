package com.app.fuse.ui.mainScreen.nearbyusers.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide
import com.app.fuse.ui.mainScreen.nearbyusers.model.NearByUserData
import kotlinx.android.synthetic.main.list_search_view.view.*

class NearByUsersAdapter : RecyclerView.Adapter<NearByUsersAdapter.ViewHolder>() {
    var layoutView = 0

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)


    private val differCallBack = object : DiffUtil.ItemCallback<NearByUserData>() {
        override fun areItemsTheSame(oldItem: NearByUserData, newItem: NearByUserData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: NearByUserData,
            newItem: NearByUserData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (layoutView == 0) {
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_nearbyfriends_single, parent, false)
            )
        } else {
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_search_view, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.itemView.apply {

            Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch()).load(model.profile)
                .into(profileImageSearch)
            userNameSearch.text = model.username
            userAgeSearch.text = model.age.toString()

            setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("userID", model.id)
                findNavController().navigate(R.id.action_nearByFriends_to_SingleUserDetails, bundle)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return if (layoutView == 0) {
            layoutView
        } else {
            layoutView
        }

    }

    fun getLayoutType(layout: Int) {
        layoutView = layout
    }
}