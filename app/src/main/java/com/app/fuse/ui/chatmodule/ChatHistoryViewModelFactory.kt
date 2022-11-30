package com.app.fuse.ui.chatmodule

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class ChatHistoryViewModelFactory(val app: Application, val repository: BinjaRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatHistoryViewModel(app,repository) as T
    }
}