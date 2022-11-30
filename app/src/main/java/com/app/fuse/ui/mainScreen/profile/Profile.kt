package com.app.fuse.ui.mainScreen.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fuse.R
import com.app.fuse.databinding.FragmentProfileBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.homescreen.home.HomeViewModel
import com.app.fuse.ui.homescreen.home.HomeViewModelFactory
import com.app.fuse.ui.homescreen.profile.adapter.RequestReceivedAdapter
import com.app.fuse.ui.homescreen.profile.adapter.RequestSentAdapter
import com.app.fuse.ui.homescreen.profile.model.RequestReceivedUserData
import com.app.fuse.ui.homescreen.profile.model.RequestSentUserData
import com.app.fuse.ui.login.model.RegisterData
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendListData
import com.app.fuse.ui.mainScreen.profile.adapter.ProfileFriendsListAdapter
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.getPlaceHolder
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject


class Profile : Fragment(R.layout.fragment_profile), View.OnClickListener{
    lateinit var binding: FragmentProfileBinding
    private lateinit var session: SessionManager
    private var requestSentAdapter: RequestSentAdapter = RequestSentAdapter()
    private var requestReceivedAdapter: RequestReceivedAdapter = RequestReceivedAdapter()
    private var friendListAdapter: ProfileFriendsListAdapter = ProfileFriendsListAdapter()
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var pageNo = 1
    private var requestReceivedUserData: List<RequestReceivedUserData> = ArrayList()
    private var requestSentUserData: List<RequestSentUserData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        init()
        setRecycleViews()
        setViewModel()
        setClickListners()
    }

    private fun setViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))

        val profileViewModelFactory = ProfileViewModelFactory(requireActivity().application, binjaRepository)

        profileViewModel = ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)
        profileViewModel.receivedRequest(requireActivity(), session.token!!)
        profileViewModel.sentRequest(requireActivity(), session.token!!)
        val homeViewModelFactory = HomeViewModelFactory(requireActivity().application, binjaRepository)

        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        val jsonObject = JsonObject()
        jsonObject.addProperty("page", pageNo)
        homeViewModel.FriendsList(requireActivity(), session.token!!, jsonObject)
        setObservers()
    }

    private fun setObservers() {
        profileViewModel.sentRequestedUser.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    if (response.data!!.status) {
                        setSentRequestData(response.data.data)
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

        profileViewModel.receivedRequestedUser.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())

                    if (response.data!!.status) {
                        setReceivedRequestData(response.data.data)
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

        homeViewModel.homeFriendsList.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())

                    if (response.data!!.status) {
                        setFriendsList(response.data.data)
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


    private fun setFriendsList(friendListData: FriendListData) {
        if (friendListData.count!! <= 0) {
            binding.friendsLayout.noFriendsText.visibility = View.VISIBLE
            binding.friendsLayout.friendsListRecycleProfile.visibility = View.GONE
        } else {
            binding.friendsLayout.noFriendsText.visibility = View.GONE
            binding.friendsLayout.friendsListRecycleProfile.visibility = View.VISIBLE
            friendListAdapter.differ.submitList(friendListData.rows)
            binding.friendsLayout.friendsListRecycleProfile.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = friendListAdapter
                friendListAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun setSentRequestData(sentRequestData: List<RequestSentUserData>) {
        if (sentRequestData.isNotEmpty()) {
            binding.requestLayout.sentRequestLayout.visibility = View.VISIBLE
            binding.requestLayout.noRequestsText.visibility = View.GONE
            requestSentUserData = sentRequestData
            requestSentAdapter.differ.submitList(sentRequestData)
            binding.requestLayout.sentRequestList.adapter = requestSentAdapter
        } else {
            binding.requestLayout.sentRequestLayout.visibility = View.GONE
        }

        if (!binding.requestLayout.sentRequestLayout.isVisible && !binding.requestLayout.receivedRequestLayout.isVisible) {
            binding.requestLayout.noRequestsText.visibility = View.VISIBLE
        }
    }

    private fun setReceivedRequestData(receivedRequestData: List<RequestReceivedUserData>) {
        if (receivedRequestData.isNotEmpty()) {
            binding.requestLayout.receivedRequestLayout.visibility = View.VISIBLE
            binding.requestLayout.noRequestsText.visibility = View.GONE
            requestReceivedUserData = receivedRequestData
            requestReceivedAdapter.differ.submitList(receivedRequestData)
            binding.requestLayout.receivedRequestList.adapter = requestReceivedAdapter
        } else {
            binding.requestLayout.receivedRequestLayout.visibility = View.GONE
        }
        if (!binding.requestLayout.sentRequestLayout.isVisible && !binding.requestLayout.receivedRequestLayout.isVisible) {
            binding.requestLayout.noRequestsText.visibility = View.VISIBLE
        }
    }

    private fun init() {
        session = SessionManager(requireContext())
        val loginUserData = session.userLoginData
        setProfileDetails(loginUserData!!)

        friendListAdapter.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("friendID", it)
            bundle.putString("from", "start")
            findNavController().navigate(R.id.fragmnetMovieMatchedList, bundle)
        }
    }




    @SuppressLint("SetTextI18n")
    private fun setProfileDetails(loginUserData: String) {
        val gson = Gson()
        val loginData: RegisterData = gson.fromJson(loginUserData, RegisterData::class.java)

        Glide.with(requireContext()).setDefaultRequestOptions(getPlaceHolder()).load(loginData.profile).into(binding.profileHeader.profileImage)

        binding.profileHeader.profileName.text = loginData.username
        binding.profileHeader.profileAge.text = "${getString(R.string.profile_age_lbl)} ${loginData.age}"

        if (loginData.designation == null) {
            binding.profileHeader.circleView.visibility = View.GONE
            binding.profileHeader.designationList.text = ""
        } else {
            binding.profileHeader.circleView.visibility = View.VISIBLE
            binding.profileHeader.designationList.text = loginData.designation
        }
    }

    private fun setClickListners() {
        binding.profileHeader.requestTxt.setOnClickListener(this)
        binding.profileHeader.friendsTxt.setOnClickListener(this)
        binding.profileSettings.setOnClickListener(this)
        binding.profileHeader.edtProfileCV.setOnClickListener(this)


        requestReceivedAdapter.setOnAcceptRequestListener {
            val jsonObject = JsonObject()
            jsonObject.addProperty("action", "accept")
            jsonObject.addProperty("id", it)

            profileViewModel.friendRequestAction(
                requireActivity(),
                session.token!!,
                jsonObject
            )
        }
        requestReceivedAdapter.setOnRejectRequestListener {
            val jsonObject = JsonObject()
            jsonObject.addProperty("action", "reject")
            jsonObject.addProperty("id", it)

            profileViewModel.friendRequestAction(
                requireActivity(),
                session.token!!,
                jsonObject
            )
        }
        requestSentAdapter.setOnonCancelRequestListener {
            val jsonObject = JsonObject()
            jsonObject.addProperty("action", "reject")
            jsonObject.addProperty("id", it)

            profileViewModel.friendRequestAction(
                requireActivity(),
                session.token!!,
                jsonObject
            )
        }

        profileViewModel.friendRequestAction.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data!!.status) {
                        requireContext().showToast(response.data.msg)
                        profileViewModel.receivedRequest(requireActivity(), session.token!!)
                        profileViewModel.sentRequest(requireActivity(), session.token!!)
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


    private fun setRecycleViews() {
        binding.requestLayout.sentRequestList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = requestSentAdapter
            requestSentAdapter.notifyDataSetChanged()
        }

        binding.requestLayout.receivedRequestList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = requestReceivedAdapter
            requestReceivedAdapter.notifyDataSetChanged()
        }

        binding.friendsLayout.friendsListRecycleProfile.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendListAdapter
            friendListAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.requestTxt -> onClickRequest()
            R.id.friendsTxt -> onClickFriends()
            R.id.profileSettings -> onClickProfileSettings()
            R.id.edtProfileCV -> onClickProfileEdit()
        }
    }

    private fun onClickProfileEdit() {
        findNavController().navigate(R.id.editProfileFragment)
    }

    private fun onClickProfileSettings() {
        val popup = PopupMenu(requireContext(), binding.profileSettings).apply {
            menuInflater.inflate(R.menu.settings_menu, menu)
            setOnMenuItemClickListener { item->
                when(item.itemId){
                    R.id.privacyPolicy->{

                    }
                    R.id.logout->{
                        profileViewModel.logOut(requireActivity(),session.token.toString())

                        profileViewModel.logOut.observe(viewLifecycleOwner, { response->
                        when(response){
                            is Resources.Success->{
                                if(response.data!!.status){
                                    binding.progressBar.root.visibility = View.GONE
                                    session.setLogin(false)
                                    session.generalEditor.clear()
                                    session.generalEditor.commit()
                                    val graph = findNavController().navInflater.inflate(R.navigation.navigation_graph)
                                    graph.startDestination = R.id.login
                                    findNavController().graph = graph
                                }else{
                                    requireContext().showToast(response.data.msg)
                                }

                            }
                            is Resources.Loading->{
                                binding.progressBar.root.visibility = View.VISIBLE
                            }
                            is Resources.Error->{
                                binding.progressBar.root.visibility = View.GONE
                                requireContext().showToast("Something Went Wrong!")
                            }
                        }

                        })

                    }
                }
                false
            }
        }


        popup.show()
    }

    private fun onClickFriends() {
        binding.profileHeader.friendsTxt.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.red
            )
        )
        binding.profileHeader.requestTxt.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        binding.friendsLayout.root.visibility = View.VISIBLE
        binding.requestLayout.root.visibility = View.GONE
    }

    private fun onClickRequest() {
        binding.profileHeader.requestTxt.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.red
            )
        )
        binding.profileHeader.friendsTxt.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        binding.requestLayout.root.visibility = View.VISIBLE
        binding.friendsLayout.root.visibility = View.GONE

    }




}

