package com.app.fuse.ui.movieswipe.model

data class MovieVotingModel(
    val data: MovieVotingData,
    val movie_match:Int,
    val msg: String,
    val status: Boolean = false
)

data class MovieVotingData(
    val is_matched: Boolean
)


