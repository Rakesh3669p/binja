package com.app.fuse.ui.movieswipe.model

import java.io.Serializable

data class MovieModel(
    val `data`: MovieData,
    val msg: String,
    val status: Boolean
) : Serializable

data class MovieData(
    val count: Int,
    val rows: List<MovieList>
)

data class MovieList(
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

data class MoviType(
    val id: Int,
    val movies_type_name: String
)
