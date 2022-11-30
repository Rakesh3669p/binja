package com.app.fuse.ui.signUp

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class SignUpDirections private constructor() {
  public companion object {
    public fun actionSignUpToHome(): NavDirections =
        ActionOnlyNavDirections(R.id.action_signUp_to_home)
  }
}
