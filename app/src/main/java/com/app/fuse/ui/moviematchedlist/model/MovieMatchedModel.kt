package com.app.fuse.ui.moviematchedlist.model

data class MovieMatchedModel(
    val data: List<MovieList>,
    val msg: String,
    val status: Boolean
)

data class MovieList(
    val created_at: String,
    val id: Int,
    val movie: Movie,
    val movie_id: Int,
    val updated_at: String,
    val user_id: Int,
    val vote: String
)

data class Movie(
    val created_at: String,
    val id: Int,
    val movie_image: String,
    val movie_name: String,
    val movie_rating: String,
    val movie_description: String
)

