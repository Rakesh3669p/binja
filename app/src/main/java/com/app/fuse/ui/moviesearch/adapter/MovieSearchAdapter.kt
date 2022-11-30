package com.app.fuse.ui.moviesearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.ListMovieSearchBinding
import com.app.fuse.utils.common.getPlaceHolder
import com.bumptech.glide.Glide
import com.app.fuse.ui.moviesearch.model.MovieSearchedList
import kotlinx.android.synthetic.main.list_movie_search.view.*

class MovieSearchAdapter : RecyclerView.Adapter<MovieSearchAdapter.ViewHolder>() {
    lateinit var context: Context

    inner class ViewHolder(val binding: ListMovieSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MovieSearchedList) {
            with(binding) {
                Glide.with(context).setDefaultRequestOptions(getPlaceHolder())
                    .load(data.movie_image)
                    .into(movieImageOnSearch)
                movieTitleOnSearch.text = data.movie_name
                movieRatingOnSearch.text =
                    "${context.getString(R.string.movie_rating)}: ${data.movie_rating}"
                movieGenreOnSearch.text = data.movie_description
                shareBtnMovieOnSearch.setOnClickListener {
                     onShareClickListener?.let { it(data) }

                }
                movieSearchLay.setOnClickListener {
                    onItemClickListener?.let { it(data.id) }
                }


            }

        }
    }
    private var onShareClickListener: ((MovieSearchedList) -> Unit)? = null

    fun setOnShareClickListener(listener: (MovieSearchedList) -> Unit) {
        onShareClickListener = listener
    }
   private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<MovieSearchedList>() {
        override fun areItemsTheSame(
            oldItem: MovieSearchedList,
            newItem: MovieSearchedList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MovieSearchedList,
            newItem: MovieSearchedList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSearchAdapter.ViewHolder {

        context = parent.context
        return ViewHolder(ListMovieSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MovieSearchAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
}