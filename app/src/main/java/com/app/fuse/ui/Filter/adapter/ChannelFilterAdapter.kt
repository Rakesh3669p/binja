package com.app.fuse.ui.Filter.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.ListMovieFilterBinding
import com.app.fuse.utils.SessionManager
import com.app.fuse.ui.Filter.model.ChannelData

class ChannelFilterAdapter : RecyclerView.Adapter<ChannelFilterAdapter.ViewHolder>() {
    var channelIDs: ArrayList<Int> = ArrayList()
    lateinit var context:Context
    lateinit var session:SessionManager
    inner class ViewHolder(val binding: ListMovieFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ChannelData) {
            with(binding) {
                channelName.text = data.subscribe_channel_name

                if (channelIDs.contains(data.id)) {
                    channelNameLayout.background =  context.getDrawable(R.drawable.channel_listview_bg)
                } else {
                    channelNameLayout.background = null
                }

                channelNameLayout.setOnClickListener {
                    if (channelIDs.contains(data.id)) {
                        channelNameLayout.background = null
                        channelIDs.remove(data.id)
                        onItemClickListener?.let { it(channelIDs) }

                    } else {
                        channelNameLayout.background =  context.getDrawable(R.drawable.channel_listview_bg)
                        channelIDs.add(data.id)
                        onItemClickListener?.let { it(channelIDs) }
                    }
                }
            }
        }
    }

    private val diffCallBack: DiffUtil.ItemCallback<ChannelData> =
        object : DiffUtil.ItemCallback<ChannelData>() {
            override fun areItemsTheSame(oldItem: ChannelData, newItem: ChannelData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ChannelData,
                newItem: ChannelData
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChannelFilterAdapter.ViewHolder {
        context = parent.context
        session = SessionManager(context)
        channelIDs = session.channelfilters!!.map { it.toInt() } as ArrayList<Int>
        return ViewHolder(
            ListMovieFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private var onItemClickListener: ((ArrayList<Int>) -> Unit)? = null

    override fun onBindViewHolder(holder: ChannelFilterAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    fun setOnItemClickListener(listener: (ArrayList<Int>) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun SelectAll(selectesIds:ArrayList<Int>){
        channelIDs = selectesIds
        Log.d("TAG","Channel $channelIDs")
        notifyDataSetChanged()
    }
}