package com.app.fuse.ui.mainScreen.search.model


data class InsertedSearchedDataModel(
    val data: List<SearchList>,
    val msg: String,
    val status: Any?
)

data class InsertedSearchedDataList(
    val age: Int,
    val created_at: String,
    val deleted_at: Any?,
    val email: String,
    val id: Int,
    val is_friend: IsFriend,
    val is_online: Int,
    val is_verified: Int,
    val profile: Any?,
    val requests: RequestData,
    val socket_id: Any?,
    val status: Int,
    val updated_at: String,
    val username: String
)
