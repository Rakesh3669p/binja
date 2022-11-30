package com.app.fuse.ui.chatmodule.model

data class ChatCountModel(
    val `data`: ChatCount,
    val msg: String,
    val status: Boolean
)


data class ChatCount(
    val unReadTotal: Int?=0
)