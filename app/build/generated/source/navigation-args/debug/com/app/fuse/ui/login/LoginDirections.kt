package com.app.fuse.ui.login

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class LoginDirections private constructor() {
  public companion object {
    public fun actionLoginToSignUp(): NavDirections =
        ActionOnlyNavDirections(R.id.action_login_to_signUp)

    public fun actionLoginToForgotPassword(): NavDirections =
        ActionOnlyNavDirections(R.id.action_login_to_forgotPassword)

    public fun actionLoginToHome(): NavDirections =
        ActionOnlyNavDirections(R.id.action_login_to_home)

    public fun actionLoginToChannelSelection(): NavDirections =
        ActionOnlyNavDirections(R.id.action_login_to_channelSelection)
  }
}
