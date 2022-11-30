package com.app.fuse.ui.SingleUserView.Model

data class SingleUserModel(
    val data: UserData,
    val msg: String,
    val status: Boolean
)

data class UserData(
    val age: Int?,
    val created_at: String,
    val email: String,
    val id: Int,
    val is_friend: IsFriend?,
    val profile: String?,
    val status: Int,
    val updated_at: String,
    val username: String,
    val designation: String,
)

data class IsFriend(
    val id: Int?,
    val request_sender: Int?,
    val request_receiver: Int?,
    val status: String?,
    val created_at: String?,
    val updated_at: String?
)
