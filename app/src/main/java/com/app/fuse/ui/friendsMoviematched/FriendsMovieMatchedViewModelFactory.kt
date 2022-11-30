package com.app.fuse.ui.friendsMoviematched

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class FriendsMovieMatchedViewModelFactory(val app: Application, val repository: BinjaRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FriendsMovieMatchedViewModel(app, repository) as T
    }
}