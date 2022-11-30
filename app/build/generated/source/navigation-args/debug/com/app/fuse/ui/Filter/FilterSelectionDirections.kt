package com.app.fuse.ui.Filter

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class FilterSelectionDirections private constructor() {
  public companion object {
    public fun actionFilterSelectionToFilterFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_filterSelection_to_filterFragment)
  }
}
