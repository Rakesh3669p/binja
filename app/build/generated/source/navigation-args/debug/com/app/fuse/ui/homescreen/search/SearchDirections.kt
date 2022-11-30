package com.app.fuse.ui.homescreen.search

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class SearchDirections private constructor() {
  public companion object {
    public fun actionSearchFragmentToSearchUserView(): NavDirections =
        ActionOnlyNavDirections(R.id.action_searchFragment_to_searchUserView)
  }
}
