package com.app.fuse.ui.mainScreen.search.model

import java.io.Serializable

data class SearchModel(
    val msg: String,
    val data: SearchData,
    val status: Boolean
)

data class SearchData(
    val count: Int,
    val rows: List<SearchList>
)


data class SearchList(
    val id: Int?,
    val age: Int?,
    val created_at: String?,
    val deleted_at: String?,
    val email: String?,
    val fcm_token: String?,
    val socket_id: String?,
    val is_verified: Any?,
    val password: String?,
    val profile: String?,
    val reset_token: String?,
    val status: String?,
    val updated_at: String?,
    val username: String,
    val is_friend:IsFriend?,
    val requests:RequestData?
) : Serializable

data class RequestData (
    val request_sender:String?,
    val request_receiver:String
)

data class IsFriend(
    val id: Int?,
    val request_sender: Int?,
    val request_receiver: Int?,
    val status: String?,
    val created_at: String?,
    val updated_at: String?
)