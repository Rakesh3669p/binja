package com.app.fuse.ui.mainScreen.profile.editProfile

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class EditProfileDirections private constructor() {
  public companion object {
    public fun actionEditProfileFragmentToProfileFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_editProfileFragment_to_profileFragment)
  }
}
