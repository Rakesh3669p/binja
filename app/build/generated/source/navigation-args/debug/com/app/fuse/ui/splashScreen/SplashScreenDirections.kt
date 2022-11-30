package com.app.fuse.ui.splashScreen

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class SplashScreenDirections private constructor() {
  public companion object {
    public fun actionSplashScreenToWelcome(): NavDirections =
        ActionOnlyNavDirections(R.id.action_splashScreen_to_welcome)
  }
}
