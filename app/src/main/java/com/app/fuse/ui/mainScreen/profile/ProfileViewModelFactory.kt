package com.app.fuse.ui.mainScreen.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(val app:Application, val repository: BinjaRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(app,repository) as T
    }
}