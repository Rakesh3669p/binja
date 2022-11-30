package com.app.fuse.ui.movieswipe

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.app.fuse.R

public class MovieFragmentDirections private constructor() {
  public companion object {
    public fun actionMovieFragmentToMovieSearch(): NavDirections =
        ActionOnlyNavDirections(R.id.action_movieFragment_to_movieSearch)

    public fun actionMovieFragmentToFriendsMovieMatched(): NavDirections =
        ActionOnlyNavDirections(R.id.action_movieFragment_to_friendsMovieMatched)
  }
}
