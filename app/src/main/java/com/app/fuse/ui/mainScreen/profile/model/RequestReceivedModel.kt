package com.app.fuse.ui.homescreen.profile.model

class RequestReceivedModel (
    val `data`: List<RequestReceivedUserData>,
    val msg: String,
    val status: Boolean
)

data class RequestReceivedUserData(
    val created_at: String,
    val id: Int,
    val request_receiver: Int,
    val request_sender_user: RequestReceivedUser,
    val request_sender: Int,
    val status: Int,
    val updated_at: String
)


data class RequestReceivedUser(
    val id: Int,
    val username: String
)