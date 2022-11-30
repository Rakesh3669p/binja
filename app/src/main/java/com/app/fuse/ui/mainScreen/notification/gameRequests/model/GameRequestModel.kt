package com.app.fuse.ui.mainScreen.notification.gameRequests.model

data class GameRequestModel(
    val data: List<GameRequestData>,
    val msg: String,
    val status: Boolean
)

data class GameRequestData(
    val created_at: String,
    val filter: String,
    val from_user_id: Int,
    val from_users_data: FromUsersData,
    val id: Int,
    val status: Int,
    val to_user_id: Int,
    val to_users_data: ToUsersData,
    val updated_at: String
)

data class FromUsersData(
    val designation: String?="",
    val id: Int?=0,
    val is_online: Boolean?=false,
    val match_in_progress: Int?=0,
    val match_with: Int?=0,
    val profile: Any?="",
    val username: String?=""
)

data class ToUsersData(
    val designation: String?="",
    val id: Int?=0,
    val is_online: Boolean?=false,
    val match_in_progress: Int?=0,
    val match_with: Int?=0,
    val profile: Any?="",
    val username: String?=""
)