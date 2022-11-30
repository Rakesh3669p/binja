package com.app.fuse.ui.mainScreen.notification.messages.model

data class ConversationModel(
    val `data`: List<ConversationList>,
    val msg: String,
    val status: Boolean
)

data class ConversationList(
    val id: Int,
    val message: String,
    val profile: String?,
    val un_read: Int,
    val designation: String,
    val username: String,
    val created_at:String,
    val time:String
)