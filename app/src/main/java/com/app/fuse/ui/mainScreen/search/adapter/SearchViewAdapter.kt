package com.app.fuse.ui.homescreen.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide
import com.app.fuse.ui.mainScreen.search.model.SearchList
import kotlinx.android.synthetic.main.list_search_view.view.*

class SearchViewAdapter : RecyclerView.Adapter<SearchViewAdapter.ViewHolder>() {
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
    ): SearchViewAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_search_view, parent, false)
        )
    }

    private var onItemClickListener: ((SearchList) -> Unit)? = null

    override fun onBindViewHolder(holder: SearchViewAdapter.ViewHolder, position: Int) {

        val model = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch()).load("${model.profile}").into(profileImageSearch)
            userNameSearch.text = model.username
            if(model.age!=null){
                userAgeSearch.text = "${resources.getString(R.string.age_lbl)}: ${model.age}"
            }else{
                userAgeSearch.text = "${resources.getString(R.string.age_lbl)}: "
            }


            if (model.is_friend!!.status != null) {
                when (model.is_friend.status) {
                    "pending" -> {

                        if (model.id == model.is_friend.request_sender) {
                            pendingRequestCVSearch.visibility = View.GONE
                            sendRequestCVSearch.visibility = View.GONE
                            acceptRequestCVSearch.visibility = View.VISIBLE
                            sendMessageCVSearch.visibility = View.GONE
                        } else {
                            pendingRequestCVSearch.visibility = View.VISIBLE
                            sendRequestCVSearch.visibility = View.GONE
                            acceptRequestCVSearch.visibility = View.GONE
                            sendMessageCVSearch.visibility = View.GONE
                        }


                    }
                    "decline" -> {
                        sendRequestCVSearch.visibility = View.VISIBLE
                        pendingRequestCVSearch.visibility = View.GONE
                        acceptRequestCVSearch.visibility = View.GONE
                        sendMessageCVSearch.visibility = View.GONE
                    }
                    "accept" -> {
                        sendRequestCVSearch.visibility = View.GONE
                        pendingRequestCVSearch.visibility = View.GONE
                        acceptRequestCVSearch.visibility = View.GONE
                        sendMessageCVSearch.visibility = View.VISIBLE

                    }
                    else -> {
                        sendRequestCVSearch.visibility = View.GONE
                        pendingRequestCVSearch.visibility = View.GONE
                        acceptRequestCVSearch.visibility = View.GONE
                        sendMessageCVSearch.visibility = View.GONE

                    }
                }
            } else {
                sendRequestCVSearch.visibility = View.VISIBLE
                pendingRequestCVSearch.visibility = View.GONE
                acceptRequestCVSearch.visibility = View.GONE
                sendMessageCVSearch.visibility = View.GONE

            }


            setOnClickListener {
                onItemClickListener?.let { it(model) }
            }
        }

    }

    fun setOnItemClickListener(listener: (SearchList) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int = differ.currentList.size
}