package com.app.fuse.ui.moviesearch

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class MovieSearchViewModelFactory(val app:Application,val repository: BinjaRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieSearchViewModel(app,repository) as T
    }
}