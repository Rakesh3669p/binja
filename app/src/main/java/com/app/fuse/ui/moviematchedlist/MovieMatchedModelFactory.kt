package com.app.fuse.ui.moviematchedlist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class MovieMatchedModelFactory(val app: Application, val binjaRepository: BinjaRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieMatchedViewModel(app, binjaRepository) as T
    }
}