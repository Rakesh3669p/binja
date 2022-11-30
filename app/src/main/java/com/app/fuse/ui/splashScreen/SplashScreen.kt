package com.app.fuse.ui.splashScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.app.fuse.R
import com.app.fuse.utils.SessionManager
import kotlinx.android.synthetic.main.activity_main.*


class SplashScreen : Fragment(R.layout.fragment_splash_screen){
    lateinit var session: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}