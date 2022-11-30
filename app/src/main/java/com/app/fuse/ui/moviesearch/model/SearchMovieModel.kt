package com.app.fuse.ui.moviesearch.model

data class SearchMovieModel(
    val `data`: MovieSearchData,
    val msg: String,
    val status: Boolean
)

data class MovieSearchData(
    val count: Int,
    val rows: List<MovieSearchedList>
)

data class MoviType(
    val id: Int,
    val movies_type_name: String
)

data class MovieSearchedList(
    val created_at: String,
    val deleted_at: Any?,
    val id: Int,
    val movi_subscribe_channel: MoviSubscribeChannel,
    val movi_type: MoviType,
    val movie_description: String,
    val movie_image: String,
    val movie_name: String,
    val movie_rating: String,
    val movie_subscribe_channel: Int,
    val movie_title: Any?,
    val movie_type: Int,
    val updated_at: String
)

data class MoviSubscribeChannel(
    val id: Int,
    val subscribe_channel_name: String
)