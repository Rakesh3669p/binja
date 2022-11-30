package com.app.fuse.ui.mainScreen.nearbyusers.model

data class NearByUsersModel(
    val `data`: List<NearByUserData>,
    val msg: String,
    val status: Boolean
)

data class NearByUserData(
    val age: Int?,
    val created_at: String,
    val deleted_at: Any?,
    val email: String,
    val id: Int,
    val is_friend: Any?,
    val is_verified: Int,
    val latitude: String,
    val longitude: String,
    val profile: String?,
    val status: Int,
    val updated_at: String,
    val username: String
)