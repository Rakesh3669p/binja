package com.app.fuse.ui.movieswipe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.databinding.ListMovieStackBinding
import com.app.fuse.ui.movieswipe.model.MovieList
import com.app.fuse.utils.common.getPlaceHolderMovie
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_movie_stack.view.*


class MovieAdapter : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    lateinit var context: Context

    inner class ViewHolder(val binding: ListMovieStackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieList) {
            with(binding) {

                Glide.with(context).setDefaultRequestOptions(getPlaceHolderMovie()).load("${data.movie_image}").into(movieImage)
                movieTitle.text = data.movie_name
                movieRating.text = "rating : ${data.movie_rating}"
                movieDesc.text = data.movie_description

            }
        }
    }


    private val differCallBack = object : DiffUtil.ItemCallback<MovieList>() {
        override fun areItemsTheSame(oldItem: MovieList, newItem: MovieList): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MovieList,
            newItem: MovieList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ListMovieStackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: MovieAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
}
