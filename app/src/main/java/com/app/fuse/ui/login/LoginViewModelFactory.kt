package com.app.fuse.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.fuse.network.BinjaRepository

class LoginViewModelFactory(val app:Application, val repository: BinjaRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(app,repository) as T
    }
}