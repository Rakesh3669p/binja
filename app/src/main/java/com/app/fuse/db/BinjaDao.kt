package com.app.fuse.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendDetails


@Dao
interface BinjaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnGoingMatchFriends(user:FriendDetails):Long

    @Query("SELECT * FROM friends_users")
    fun getOnGoingMatchFriends():LiveData<List<FriendDetails>>

    @Delete
    suspend fun deleteOnGoingMatchFriends(user:FriendDetails)


}
