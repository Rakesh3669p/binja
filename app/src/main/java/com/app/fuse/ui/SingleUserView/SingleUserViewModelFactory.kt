package com.app.fuse.ui.SingleUserView

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class SingleUserViewModelFactory(val app: Application, val binjaRepository: BinjaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SingleUserViewModel(app, binjaRepository) as T
    }
}