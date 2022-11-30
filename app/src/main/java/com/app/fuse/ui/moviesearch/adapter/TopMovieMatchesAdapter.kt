package com.app.fuse.ui.moviesearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.utils.common.getPlaceHolder
import com.bumptech.glide.Glide
import com.app.fuse.ui.moviesearch.model.TopMovieMatchData
import kotlinx.android.synthetic.main.list_top_movie_matches.view.*

class TopMovieMatchesAdapter : RecyclerView.Adapter< TopMovieMatchesAdapter .ViewHolder>() {
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)


    private val diffCallBack = object : DiffUtil.ItemCallback<TopMovieMatchData>() {
        override fun areItemsTheSame(oldItem: TopMovieMatchData, newItem: TopMovieMatchData): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(
            oldItem: TopMovieMatchData,
            newItem: TopMovieMatchData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopMovieMatchesAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_top_movie_matches, parent, false)
        )
    }

    private var onShareClickListener: ((TopMovieMatchData) -> Unit)? = null
    private var onItemClickListener: ((Int) -> Unit)? = null



    override fun onBindViewHolder(holder: TopMovieMatchesAdapter.ViewHolder, position: Int) {
        val model = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(context).setDefaultRequestOptions(getPlaceHolder()).load(model.movie_image).into(movieImageTopMatch)
            if(model.movie_name==null){
                movieTitleTopMatch.text = "Moviie Title"
            }else{
                movieTitleTopMatch.text = model.movie_name
            }

            movieRatingTopMatch.text ="${context.getString(R.string.movie_rating)}: ${model.movie_rating}"
            movieDescTopMatch.text =model.movie_description

            shareCV.setOnClickListener { onShareClickListener?.let { it(model) } }
            topMovieMatchLayout.setOnClickListener { onItemClickListener?.let { it(model.id) } }
        }
    }

    fun setOnShareClickListener(listener: (TopMovieMatchData) -> Unit) {
        onShareClickListener = listener
    }

    fun setOnItemClickListner(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }
    override fun getItemCount(): Int = differ.currentList.size
}