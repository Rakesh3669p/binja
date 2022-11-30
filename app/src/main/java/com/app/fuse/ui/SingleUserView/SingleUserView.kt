package com.app.fuse.ui.SeachUseView

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.fuse.R
import com.app.fuse.databinding.FragmentSingleUserViewBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.mainScreen.profile.ProfileViewModel
import com.app.fuse.ui.mainScreen.profile.ProfileViewModelFactory

import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.getPlaceHolder
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.bumptech.glide.Glide
import com.app.fuse.ui.SingleUserView.Model.UserData
import com.app.fuse.ui.SingleUserView.SingleUserViewModel
import com.app.fuse.ui.SingleUserView.SingleUserViewModelFactory
import com.app.fuse.ui.chatmodule.ChatActivity
import com.google.gson.JsonObject



class SingleUserView : Fragment(R.layout.fragment_single_user_view), View.OnClickListener {
    lateinit var binding: FragmentSingleUserViewBinding
    lateinit var session: SessionManager
    lateinit var singleUserViewModel: SingleUserViewModel
    var userID = 0
    var friendUserID = 0

    lateinit var profileViewModel: ProfileViewModel
    lateinit var userData: UserData
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSingleUserViewBinding.bind(view)
        init()
        setViewModel()
        setOnClickListners()
    }


    private fun init() {

        session = SessionManager(requireContext())
        userID = arguments!!.getInt("userID")
    }

    private fun setViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val singleUsersViewModelFactory = SingleUserViewModelFactory(
            requireActivity().application,
            binjaRepository
        )
        val profileViewModelFactory = ProfileViewModelFactory(
            requireActivity().application,
            binjaRepository
        )
        profileViewModel =
            ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)


        singleUserViewModel = ViewModelProvider(this, singleUsersViewModelFactory).get(
            SingleUserViewModel::class.java
        )

        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", userID)

        singleUserViewModel.SingleUserModel(requireActivity(), session.token!!, jsonObject)

        setObserver()
    }

    private fun setObserver() {
        singleUserViewModel.singleUser.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    if (response.data!!.status) {
                        setUser(response.data.data)
                        if (response.data.data.is_friend != null) {
                            friendUserID = response.data.data.is_friend?.id!!
                        }

                    } else {
                        requireContext().showToast(response.data.msg)
                    }

                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    requireContext().showToast(response.message.toString())
                }
            }
        })

        profileViewModel.friendRequestAction.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data!!.status) {

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("user_id", userID)
                        singleUserViewModel.SingleUserModel(
                            requireActivity(),
                            session.token!!,
                            jsonObject
                        )

                    } else {
                        requireContext().showToast(response.data.msg)
                    }

                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    requireContext().showToast(response.message.toString())
                }
            }
        })
    }

    private fun setUser(data: UserData) {
        userData = data
        binding.singleViewUserName.visibility = View.VISIBLE
        binding.singleViewUserDesc.visibility = View.VISIBLE
        binding.singleUserViewImage.visibility = View.VISIBLE

        Glide.with(requireContext()).setDefaultRequestOptions(getPlaceHolder())
            .load("${userData.profile}").into(binding.singleUserViewImage)

        binding.singleViewUserName.text = userData.username

        when {
            userData.is_friend == null -> {
                showSendRequest()
            }
            userData.is_friend?.status == "accept" -> {
                showSendMessage()
            }
            userData.is_friend?.status == "reject" -> {
                showSendRequest()
            }
            userData.is_friend?.status == "decline" -> {
                showSendRequest()
            }
            userData.is_friend?.status == "pending" -> {
                if (userData.id == userData.is_friend?.request_sender) {
                    showRequestReceived()
                } else {
                    showRequestSent()
                }
            }
        }

    }


    private fun showSendRequest() {
        binding.sendRequestCV.visibility = View.VISIBLE
        binding.sendMesageCV.visibility = View.GONE
        binding.requestSentLayout.visibility = View.GONE
        binding.requestReceivedLayout.visibility = View.GONE
    }

    private fun showSendMessage() {
        binding.sendMesageCV.visibility = View.VISIBLE
        binding.requestSentLayout.visibility = View.GONE
        binding.sendRequestCV.visibility = View.GONE
        binding.requestReceivedLayout.visibility = View.GONE
    }

    private fun showRequestSent() {
        binding.requestSentLayout.visibility = View.VISIBLE
        binding.sendMesageCV.visibility = View.GONE
        binding.sendRequestCV.visibility = View.GONE
        binding.requestReceivedLayout.visibility = View.GONE
    }

    private fun showRequestReceived() {
        binding.requestSentLayout.visibility = View.GONE
        binding.sendMesageCV.visibility = View.GONE
        binding.sendRequestCV.visibility = View.GONE
        binding.requestReceivedLayout.visibility = View.VISIBLE
    }

    private fun setOnClickListners() {
        binding.backArrowSingleUser.setOnClickListener(this)
        binding.sendMesageCV.setOnClickListener(this)
        binding.sendRequestCV.setOnClickListener(this)
        binding.cancelRequestCV.setOnClickListener(this)
        binding.pendingRequestCV.setOnClickListener(this)
        binding.rejectCVSingleUserView.setOnClickListener(this)
        binding.acceptCVSingleUserView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backArrowSingleUser -> {
                findNavController().navigateUp()
            }
            R.id.sendRequestCV -> {

                sendFriendRequest()
            }
            R.id.cancelRequestCV -> {
                val jsonObject = JsonObject()
                jsonObject.addProperty("action", "reject")
                jsonObject.addProperty("id", friendUserID)

                profileViewModel.friendRequestAction(
                    requireActivity(),
                    session.token!!,
                    jsonObject
                )
            }
            R.id.pendingRequestCV -> {

            }
            R.id.sendMesageCV -> {
                startActivity(Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra("friendID", userData.id)
                    putExtra("friendUserName", userData.username)
                    putExtra("friendBio", userData.designation)
                    if (userData.profile != null) {
                        putExtra("profile", userData.profile)
                    } else {
                        putExtra("profile", "")
                    }
                })

            }
            R.id.acceptCVSingleUserView -> {
                val jsonObject = JsonObject()
                jsonObject.addProperty("action", "accept")
                jsonObject.addProperty("id", friendUserID)

                profileViewModel.friendRequestAction(
                    requireActivity(),
                    session.token!!,
                    jsonObject
                )

            }
            R.id.rejectCVSingleUserView -> {
                val jsonObject = JsonObject()
                jsonObject.addProperty("action", "reject")
                jsonObject.addProperty("id", friendUserID)

                profileViewModel.friendRequestAction(
                    requireActivity(),
                    session.token!!,
                    jsonObject
                )

            }


        }
    }

    private fun sendFriendRequest() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("send_friend_request_id", userID)
        singleUserViewModel.SendFriendRequest(requireActivity(), session.token!!, jsonObject)
        setFriendRequestObserver()

    }

    private fun setFriendRequestObserver() {
        singleUserViewModel.sendFriendRequest.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
//                    pgrBarLayout.hide(requireActivity())
                    if (response.data!!.status) {
                        requireContext().showToast(response.data.msg)
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("user_id", userID)
                        singleUserViewModel.SingleUserModel(
                            requireActivity(),
                            session.token!!,
                            jsonObject
                        )
                        showRequestSent()
                    } else {
                        requireContext().showToast(response.data.msg)
                    }
                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                }
            }
        })
    }
}