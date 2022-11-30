package com.app.fuse.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.fuse.R
import com.app.fuse.databinding.FragmentLoginBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.login.model.LoginModel
import com.app.fuse.utils.Constants
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SocketManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.hideKeyboard
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson


class Login : Fragment(R.layout.fragment_login), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var session: SessionManager
    private lateinit var loginViewModel: LoginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        init()
        setClickListners()
        setUpViewModel()
    }

    private fun init() {
        session = SessionManager(requireContext())
    }

    private fun setClickListners() {
        binding.backArrow.setOnClickListener(this)
        binding.loginCV.setOnClickListener(this)
        binding.signupTxt.setOnClickListener(this)
        binding.forgotPasswordTxt.setOnClickListener(this)
        binding.signupTxt.setOnClickListener(this)
    }

    private fun setUpViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val loginViewModelFactory = LoginViewModelFactory(
            requireActivity().application,
            binjaRepository
        )
        loginViewModel =
            ViewModelProvider(this, loginViewModelFactory).get(LoginViewModel::class.java)
    }



    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backArrow -> {
                findNavController().navigateUp()
            }
            R.id.forgotPasswordTxt -> {
                findNavController().navigate(R.id.action_login_to_forgotPassword)
            }
            R.id.loginCV -> loginUser()

            R.id.signupTxt -> {
                findNavController().navigate(R.id.action_login_to_signUp)
            }
        }
    }

    private fun loginUser() {
        hideKeyboard(activity!!)
        val userName = binding.edtEmailLgn.text.toString()
        val password = binding.edtPasswordLgn.text.toString()
        if (session.fcmToken!!.isNotEmpty()) {
            loginViewModel.ValidateAndLogin(requireActivity(), userName, password)
            loginViewModel.login.observe(viewLifecycleOwner, { response ->
                when (response) {
                    is Resources.Success -> {
                        binding.progressBar.pgrBarLayout.hide(requireActivity())
                        setLogin(response.data)
                    }
                    is Resources.Loading -> {
                        binding.progressBar.pgrBarLayout.show(requireActivity())
                    }
                    is Resources.Error -> {
                        binding.progressBar.pgrBarLayout.hide(requireActivity())
                        requireContext().showToast("Error${response.message}")
                    }
                }
            })
        } else {
            getFCM()
        }
    }

    private fun getFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
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

    private fun setLogin(loginData: LoginModel?) {
        if (loginData?.status == true) {
            session.setLogin(true)
            session.token = loginData.access_token
            session.userID = loginData.data!!.id

            val gson = Gson()
            val json = gson.toJson(loginData.data)
            session.userLoginData = json

            if (session.isLoggedIn) {
                    val graph = findNavController().navInflater.inflate(R.navigation.navigation_graph)
                    graph.startDestination = R.id.homeMainFragment
                    findNavController().graph = graph
                    SocketManager.instance!!.connectSocket(session.token!!, Constants.BASE_URL)


            }
        } else {
            requireContext().showToast(loginData?.msg.toString())
        }
    }
}