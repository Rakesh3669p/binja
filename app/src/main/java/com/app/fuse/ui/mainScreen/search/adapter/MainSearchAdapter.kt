package com.app.fuse.ui.homescreen.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.ui.mainScreen.search.model.SearchList
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_search_users.view.*

class MainSearchAdapter : RecyclerView.Adapter<MainSearchAdapter.ViewHolder>() {
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)


    private val diffCallBack = object : DiffUtil.ItemCallback<SearchList>() {
        override fun areItemsTheSame(oldItem: SearchList, newItem: SearchList): Boolean {
            return oldItem == newItem
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
    ): MainSearchAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_search_users, parent, false)
        )
    }

    private var onItemClickListener: ((SearchList) -> Unit)? = null
        private var onRequestClickListner: ((Int) -> Unit)? = null

    override fun onBindViewHolder(holder: MainSearchAdapter.ViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch())
                .load("${model.profile}").into(searchListImage)
            searchListUserName.text = model.username
            if (model.is_friend!!.status != null) {
                when (model.is_friend.status) {
                    "pending" -> {
                        searchListRequestPendingCV.visibility = View.VISIBLE
                        searchListRequestCV.visibility = View.GONE
                    }
                    else -> {
                        searchListRequestCV.visibility = View.GONE
                        searchListRequestPendingCV.visibility = View.GONE
                    }
                }
            } else {
                searchListRequestCV.visibility = View.VISIBLE
                searchListRequestPendingCV.visibility = View.GONE
            }

            searchListRequestCV.setOnClickListener {
                onRequestClickListner?.let { it(model.id!!) }
            }
            setOnClickListener {
                onItemClickListener?.let { it(model) }
            }
        }

    }

    fun setOnRequestClickListener(listener: (Int) -> Unit) {
        onRequestClickListner = listener
    }

    fun setOnItemClickListener(listener: (SearchList) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int = differ.currentList.size
}