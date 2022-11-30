package com.app.fuse.ui.mainScreen.home.FriendsList

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class HomeDirections private constructor() {
  public companion object {
    public fun actionHomeFragmentToMovieFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_movieFragment)
  }
}
