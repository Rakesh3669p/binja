package com.app.fuse.ui.welcome

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class WelcomeDirections private constructor() {
  public companion object {
    public fun actionWelcomeToLogin(): NavDirections =
        ActionOnlyNavDirections(R.id.action_welcome_to_login)

    public fun actionWelcomeToSignUp(): NavDirections =
        ActionOnlyNavDirections(R.id.action_welcome_to_signUp)
  }
}
