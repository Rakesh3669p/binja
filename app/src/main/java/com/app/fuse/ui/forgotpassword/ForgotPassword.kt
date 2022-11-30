package com.app.fuse.ui.forgotpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.fuse.R
import com.app.fuse.databinding.FragmentForgotPasswordBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast

import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.progress_bar.*

class ForgotPassword : Fragment(R.layout.fragment_forgot_password), View.OnClickListener {
    private lateinit var binding: FragmentForgotPasswordBinding
    lateinit var sessionManager: SessionManager
    lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    var count =0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForgotPasswordBinding.bind(view)
        init()
        setClickListners()
        setViewModel()

    }

    private fun setViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val forgotPasswordViewModelFactory =
            ForgotPasswordViewModelFactory(requireActivity().application, binjaRepository)
        forgotPasswordViewModel = ViewModelProvider(
            this,
            forgotPasswordViewModelFactory
        ).get(ForgotPasswordViewModel::class.java)
    }

    private fun setClickListners() {
        binding.backArrowFP.setOnClickListener(this)
        binding.sendRequestCV.setOnClickListener(this)
    }

    private fun init() {
        sessionManager = SessionManager(requireContext())

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backArrowFP -> findNavController().navigateUp()
            R.id.sendRequestCV -> {
                SendRequest()
            }
        }
    }

    private fun SendRequest() {

        val userEmail = edtEmailFP.text.toString()

        if(forgotPasswordViewModel.validateDataAndForgotPassword(requireActivity(),userEmail)){

                forgotPasswordViewModel.forgotPassword.observe(
                    viewLifecycleOwner,
                    Observer { response ->
                        when (response) {
                            is Resources.Success -> {
                                pgrBarLayout.hide(activity!!)
                                requireContext().showToast(response.data?.msg.toString())
                                findNavController().navigateUp()
                            }
                            is Resources.Loading -> {
                                pgrBarLayout.show(activity!!)

                            }
                            is Resources.Error -> {
                                pgrBarLayout.hide(activity!!)
                                requireContext().showToast(response.message.toString())
                            }
                        }
                    })
        }else{
            requireContext().showToast("Invalid Email")
        }
    }
}