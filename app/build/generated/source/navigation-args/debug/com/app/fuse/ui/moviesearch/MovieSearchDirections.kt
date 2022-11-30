package com.app.fuse.ui.moviesearch

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class MovieSearchDirections private constructor() {
  public companion object {
    public fun actionFragmentMovieSearchToMovieShareFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_fragmentMovieSearch_to_movieShareFragment)
  }
}
