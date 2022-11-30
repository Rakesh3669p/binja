package com.app.fuse.ui.moviematchedlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.databinding.ListMovieMatchedBinding
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide
import com.app.fuse.ui.moviematchedlist.model.MovieList

class MovieMatchedAdapter : RecyclerView.Adapter< MovieMatchedAdapter .ViewHolder>() {
    lateinit var context: Context
    inner class ViewHolder(val binding: ListMovieMatchedBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data:MovieList){
            with(binding){
                Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch()).load(data.movie.movie_image).into(movieImageMatched)
                movieTitleMatched.text  = data.movie.movie_name
                movieRatingMatched.text  = data.movie.movie_rating
                movieDescMatched.text  = data.movie.movie_description
            }
        }

    }


    private val diffCallBack = object : DiffUtil.ItemCallback<MovieList>() {
        override fun areItemsTheSame(oldItem: MovieList, newItem: MovieList): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(
            oldItem: MovieList,
            newItem: MovieList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieMatchedAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(ListMovieMatchedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MovieMatchedAdapter.ViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int = differ.currentList.size
}