package com.app.fuse.ui.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.fuse.R
import kotlinx.android.synthetic.main.fragment_welcome.*


class Welcome : Fragment(R.layout.fragment_welcome), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginCV.setOnClickListener(this)
        signUpCV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.loginCV->{
                findNavController().navigate(R.id.action_welcome_to_login)
            }
            R.id.signUpCV->{
                findNavController().navigate(R.id.action_welcome_to_signUp)
            }
        }
    }

}