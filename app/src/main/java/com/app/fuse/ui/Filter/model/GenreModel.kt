package com.app.fuse.ui.Filter.model

data class GenreModel(
    val `data`: List<GenreData>,
    val msg: String,
    val status: Boolean
)

data class GenreData(
    val created_at: String,
    val id: Int,
    val movies_type_name: String,
    val updated_at: String
)