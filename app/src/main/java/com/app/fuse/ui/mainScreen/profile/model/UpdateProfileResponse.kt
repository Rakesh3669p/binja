package com.app.fuse.ui.mainScreen.profile.model

data class UpdateProfileResponse(
    val data: Data,
    val msg: String,
    val status: Boolean,
    val access_token: String
)

data class Data(
    val age: Int,
    val created_at: String,
    val deleted_at: Any,
    val email: String,
    val fcm_token: String,
    val id: Int,
    val is_verified: Boolean,
    val latitude: String,
    val designation: String,
    val longitude: String,
    var profile: String,
    val reset_token: String,
    val status: Int,
    val updated_at: String,
    val username: String
)
