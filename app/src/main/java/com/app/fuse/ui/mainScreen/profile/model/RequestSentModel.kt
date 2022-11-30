package com.app.fuse.ui.homescreen.profile.model


data class RequestSentModel (
    val `data`: List<RequestSentUserData>,
    val msg: String,
    val status: Boolean
)

data class RequestSentUserData(
    val created_at: String,
    val id: Int,
    val request_receiver: Int,
    val request_receiver_user: RequestSentReceiverUser,
    val request_sender: Int,
    val status: Int,
    val updated_at: String
)


data class RequestSentReceiverUser(
    val id: Int,
    val username: String
)