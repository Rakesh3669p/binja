package com.app.fuse.ui.mainScreen.home.FriendsList.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class FriendListModel(
    val data: FriendListData,
    val msg: String,
    val status: Boolean
)

data class FriendListData(
    val count: Int?=0,
    val rows: List<FriendsConnectionList>
)

data class FriendsConnectionList(
    val id: Int?,
    var request_sender: Int?,
    var request_receiver: Int?,
    val status: Int?,
    val created_at: String?,
    var match_with: String,
    val friend_user: FriendDetails?,
)

@Entity(tableName = "friends_users")
data class FriendDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=0,
    val username: String?="",
    val is_online: String?="",
    val designation: String?="",
    val profile: String?="",
    var match_in_progress: Int?=0,
    var match_with: Int?=0,
    val on_goin_match: Int?=0
):Serializable

