package com.app.fuse.ui.Filter

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class FilterDirections private constructor() {
  public companion object {
    public fun actionFilterFragmentToMovieFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_filterFragment_to_movieFragment)

    public fun actionFilterFragmentToFilterSelection(): NavDirections =
        ActionOnlyNavDirections(R.id.action_filterFragment_to_filterSelection)
  }
}
