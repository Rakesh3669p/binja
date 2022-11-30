package com.app.fuse.ui.Filter.model

import java.io.Serializable

data class ChannelModel(
    val `data`: List<ChannelData>,
    val msg: String,
    val status: Boolean
)

data class ChannelData(
    val created_at: String?,
    val id: Int,
    val subscribe_channel_name: String,
    val updated_at: String
):Serializable