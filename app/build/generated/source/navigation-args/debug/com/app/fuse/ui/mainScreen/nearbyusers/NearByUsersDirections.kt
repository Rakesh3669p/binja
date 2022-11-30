package com.app.fuse.ui.mainScreen.nearbyusers

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class NearByUsersDirections private constructor() {
  public companion object {
    public fun actionNearByFriendsToSingleUserDetails(): NavDirections =
        ActionOnlyNavDirections(R.id.action_nearByFriends_to_SingleUserDetails)
  }
}
