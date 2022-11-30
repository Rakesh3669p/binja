package com.app.fuse.ui.chatmodule.model

data class ChatHistoryModel(
    val data: ChatData,
    val msg: String,
    val status: Boolean
)

data class ChatData(
    val count: Int,
    val rows: List<ChatDataList>
)

data class ChatDataList(
    val created_at: String,
    val from_user_id: Int,
    val id: Int?,
    val message: String,
    val time: String,
    val message_postion: String,
    val profile: Any?,
    val to_user_id: Int
)
