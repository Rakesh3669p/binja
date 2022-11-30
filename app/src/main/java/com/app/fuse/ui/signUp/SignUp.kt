package com.app.fuse.ui.signUp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.fuse.R
import com.app.fuse.databinding.FragmentSignUpBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.signUp.Model.SignUpModel
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson

class SignUp : Fragment(R.layout.fragment_sign_up), View.OnClickListener {
    lateinit var session: SessionManager
    lateinit var signUpViewModel: SignUpViewModel
    private lateinit var binding: FragmentSignUpBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpBinding.bind(view)
        init()
        setClickListners()
        setViewModel()
    }

    private fun init() {
        session = SessionManager(requireContext())
    }

    private fun setViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val signUpViewModelFactory = SignUpViewModelFactory(requireActivity().application, binjaRepository)
        signUpViewModel = ViewModelProvider(this, signUpViewModelFactory).get(SignUpViewModel::class.java)
    }


    private fun setClickListners() {
        binding.backArrow.setOnClickListener(this)
        binding.signUpCV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backArrow -> {
                findNavController().navigateUp()
            }
            R.id.signUpCV -> {
                registerUser()
            }
        }
    }


    private fun registerUser() {

        val userName = binding.edtUserName.text.toString()
        val userEmail = binding.edtEmailSignUp.text.toString()
        val password = binding.edtPasswordSignUp.text.toString()
        val confirmPassword = binding.edtConfirmPasswordSignUp.text.toString()

        if (session.fcmToken!!.isNotEmpty()) {

            if (signUpViewModel.validDataAndSignUp(
                    requireActivity(),
                    userName,
                    userEmail,
                    password,
                    confirmPassword
                )
            ) {

                signUpViewModel.signUp.observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resources.Success -> {
                            binding.progressBar.pgrBarLayout.hide(activity!!)
                            setLogin(response?.data)
                        }
                        is Resources.Loading -> {
                            binding.progressBar.pgrBarLayout.show(activity!!)
                        }
                        is Resources.Error -> {
                            binding.progressBar.pgrBarLayout.hide(activity!!)
                            requireContext().showToast(response.message.toString())
                            Log.d("RegisterError", response.message.toString())
                        }
                    }
                })
            }
        } else {
            GetFCM()
        }
    }

    fun GetFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener<String?> { task ->
            if (!task.isSuccessful) {
                Log.d(
                    "TAG", "Fetching FCM registration token failed", task.exception
                )
                return@OnCompleteListener
            }

            val token = task.result
            session.fcmToken = token

        })
    }


    private fun setLogin(registerData: SignUpModel?) {
        if (registerData!!.status) {
            session.setLogin(true)
            session.token = registerData?.access_token
            session.userID = registerData?.data.id
            session.isVerified = registerData?.data?.is_verified

            val gson = Gson()
            val json = gson.toJson(registerData.data)
            session.userLoginData = json


            val graph = findNavController().navInflater.inflate(R.navigation.navigation_graph)
            graph.startDestination = R.id.homeMainFragment
            findNavController().graph = graph

        } else {
            requireContext().showToast(registerData?.msg.toString())
        }
    }

}