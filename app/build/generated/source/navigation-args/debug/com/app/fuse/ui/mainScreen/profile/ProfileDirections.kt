package com.app.fuse.ui.mainScreen.profile

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class ProfileDirections private constructor() {
  public companion object {
    public fun actionProfileFragmentToEditProfile(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_editProfile)
  }
}
