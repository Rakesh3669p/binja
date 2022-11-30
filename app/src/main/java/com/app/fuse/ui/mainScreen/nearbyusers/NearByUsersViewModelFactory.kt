package com.app.fuse.ui.mainScreen.nearbyusers

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class NearByUsersViewModelFactory(val app: Application, val repository: BinjaRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NearByUsersViewModel(app, repository) as T
    }
}