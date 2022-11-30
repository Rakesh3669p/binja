package com.app.fuse.ui.mainScreen.search.model

data class InsertedSearchResponseModel(
    val `data`:InsertedSearchResponseData,
    val msg: String,
    val status: Boolean
)

data class InsertedSearchResponseData(
    val created_at: String,
    val id: Int,
    val search: String,
    val searching_user_id: Int,
    val type: String,
    val user_id: String
)