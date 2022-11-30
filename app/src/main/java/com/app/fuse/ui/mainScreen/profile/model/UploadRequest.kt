package com.app.fuse.ui.mainScreen.profile.model

data class UploadRequest(
    val username: String,
    val age: Int,
    val profile: String,
    val latitude: String,
    val longitude: String
)