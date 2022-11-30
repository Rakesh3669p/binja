package com.app.fuse.ui.homescreen.messages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class MessageViewModelFactory(val app:Application, val repository: BinjaRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MessageViewModel(app,repository) as T
    }
}