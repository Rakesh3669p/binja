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
import com.app.fuse.ui.Filter.model.GenreData

class GenreFilterAdapter : RecyclerView.Adapter< GenreFilterAdapter .ViewHolder>() {
    var genreIDs: ArrayList<Int> = ArrayList()

    lateinit var context: Context
    lateinit var session: SessionManager
    inner class ViewHolder(val binding:ListMovieFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GenreData) {
        with(binding) {
            channelName.text = data.movies_type_name

            if (genreIDs.contains(data.id)) {
                channelNameLayout.background =  context.getDrawable(R.drawable.channel_listview_bg)
            } else {
                channelNameLayout.background = null

            }

            channelNameLayout.setOnClickListener {

                if (genreIDs.contains(data.id)) {
                    channelNameLayout.background = null
                    genreIDs.remove(data.id)
                    onItemClickListener?.let { it(genreIDs) }
                } else {
                    channelNameLayout.background =  context.getDrawable(R.drawable.channel_listview_bg)
                    genreIDs.add(data.id)
                    onItemClickListener?.let { it(genreIDs) }
                }
            }
        }
    }}


    private val diffCallBack = object : DiffUtil.ItemCallback<GenreData>() {
        override fun areItemsTheSame(oldItem: GenreData, newItem:GenreData): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(
            oldItem: GenreData,
            newItem: GenreData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenreFilterAdapter.ViewHolder {
        context = parent.context
        session = SessionManager(context)
        genreIDs = session.genrefilters!!.map { it.toInt() } as ArrayList<Int>
        return ViewHolder(ListMovieFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    private var onItemClickListener: ((ArrayList<Int>) -> Unit)? = null

    override fun onBindViewHolder(holder: GenreFilterAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
    fun setOnItemClickListener(listener: (ArrayList<Int>) -> Unit) {
        onItemClickListener = listener
    }
    override fun getItemCount(): Int = differ.currentList.size

    fun SelectAll(selectesIds:ArrayList<Int>){
        genreIDs = selectesIds
        Log.d("TAG","Channel $genreIDs")
        notifyDataSetChanged()
    }
}