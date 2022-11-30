package com.app.fuse.ui.mainScreen.notification.gameRequests

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class RequestGameViewModelFactory(val app: Application, val repository: BinjaRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RequestGameViewModel(app, repository) as T
    }
}