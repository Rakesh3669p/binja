package com.app.fuse.ui.mainScreen.home.FriendsList

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.FragmentHomeBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.service.GPSModeChangedReceiver
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendListData
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendsConnectionList
import com.app.fuse.ui.chatmodule.ChatHistoryViewModel
import com.app.fuse.ui.chatmodule.ChatHistoryViewModelFactory
import com.app.fuse.ui.homescreen.home.HomeViewModel
import com.app.fuse.ui.homescreen.home.HomeViewModelFactory
import com.app.fuse.ui.homescreen.home.adapter.FriendListAdapter
import com.app.fuse.utils.Constants
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SocketManager
import com.app.fuse.utils.common.*
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class Home : Fragment(R.layout.fragment_home), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    lateinit var navBottomView: BottomNavigationView
    lateinit var session: SessionManager
    lateinit var homeViewModel: HomeViewModel
    lateinit var chatViewModel: ChatHistoryViewModel
    lateinit var friendListAdapter: FriendListAdapter
    var pageNo = 1
    val jsonObject = JsonObject()
    private var mSocket: Socket? = null

    val freindsConnectionList: MutableList<FriendsConnectionList> = ArrayList()
    var freindsConnectionSearchedList: List<FriendsConnectionList> = ArrayList()

    var searchValue = ""

    private var firstVisibleInListview = 0

    //Location
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var gpsReceiver: GPSModeChangedReceiver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        init()
        SocketConnect()
        initializeLocation()
        setRecylerView()
        setViewModel()
        setOnClickListners()

    }



    private fun init() {
        hideKeyboard(activity!!)
        session = SessionManager(requireContext())
        friendListAdapter = FriendListAdapter()
        binding.edtSearchHome.addTextChangedListener(homeFriendWatcher)
        navBottomView = (this.context as AppCompatActivity).findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView

    }

    private fun SocketConnect() {
        SocketManager.instance!!.connectSocket(session.token!!, Constants.BASE_URL)
        mSocket = SocketManager.instance!!.getSocket()
        mSocket!!.connect()
//        mSocket!!.on("match_in_progress_listner", OnMatchInProgress)
//        mSocket!!.on("match_in_progress_disconnected_listner", OnMatchDisconnect)

        if (mSocket!!.connected()) {
            Log.d("TAG", "Socket Connected :)")

        } else {
            Log.d("TAG", "Socket Not Connected:(")
//            requireContext().showToast("Oops!! Something Went try again later")
        }
    }

    private val OnMatchInProgress: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            activity?.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                Log.d("TAG", "OnMatchProgressData......$data")
                val from_user_id: Int
                val to_user_id: Int
                val match_with: Int

                try {
                    from_user_id = data.getInt("from_user_id")
                    to_user_id = data.getInt("to_user_id")
                    match_with = data.getInt("match_with")
                    friendListAdapter.differ.currentList.forEach {
                        if (from_user_id == it.friend_user!!.id) {
                            it.request_sender = from_user_id
                            it.request_receiver = to_user_id
                            it.friend_user.match_with = match_with
                            it.friend_user.match_in_progress = 1
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("TAG", "Exception $e")
                    return@Runnable
                }

            })
        }
    }

    private val OnMatchDisconnect: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            activity?.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                Log.d("TAG", "OnMatchDisconnect......$data")
                val from_user_id: Int
                val to_user_id: Int

                try {
                    from_user_id = data.getInt("from_user_id")
                    to_user_id = data.getInt("to_user_id")
                    friendListAdapter.differ.currentList.forEach {
                        if (from_user_id == it.friend_user!!.id) {
                            it.friend_user.match_in_progress = 0
                            it.request_sender = from_user_id
                            it.request_receiver = to_user_id
                            it.friend_user.match_with = 0
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("TAG", "Exception $e")
                    return@Runnable
                }

            })
        }
    }


    private fun initializeLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 500
        locationRequest.fastestInterval = 100
        locationRequest.numUpdates = 1
        gpsReceiver = GPSModeChangedReceiver()
        GetLocation()
    }

    private fun setOnClickListners() {
        binding.movieSearch.setOnClickListener(this)
        friendListAdapter.setOnStartMatchClickListner {friendsConnextionList->

            homeViewModel.insertOnGoingMatchFriend(friendsConnextionList.friend_user!!)
        }
    }




    private fun setObserver() {
        homeViewModel.homeFriendsList.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    binding.nofriendslayout.nofreindsLayout.hide(requireActivity())
                    if (response.data!!.status) {
                        if (searchValue.isNotEmpty()) {
                            setSearchedFriends(response.data.data)
                        } else {
                            setFriendsList(response.data.data)
                        }

                    } else {
                        if (response.data.msg == "Unauthorized") {
                            session.setLogin(false)
                            val graph =
                                findNavController().navInflater.inflate(R.navigation.navigation_graph)
                            graph.startDestination = R.id.login
                            findNavController().graph = graph
                        }
                    }
                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                    binding.nofriendslayout.nofreindsLayout.hide(requireActivity())
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    binding.nofriendslayout.nofreindsLayout.hide(requireActivity())
                    requireContext().showToast(response.message.toString())
                    if (response.message == "Unauthorized") {
                        session.setLogin(false)
                        val graph =
                            findNavController().navInflater.inflate(R.navigation.navigation_graph)
                        graph.startDestination = R.id.login
                        findNavController().graph = graph
                    }
                }
            }
        })

        chatViewModel.chatCount.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data!!.status) {
                        if (response.data.data.unReadTotal!! <= 0) {
                            navBottomView.getOrCreateBadge(R.id.messagesFragment).apply {
                                isVisible = false
                            }
                        } else {
                            session.messageCount = response.data.data.unReadTotal
                            navBottomView.getOrCreateBadge(R.id.messagesFragment).apply {
                                number = session.messageCount!!
                                isVisible = true
                            }
                        }
                    }
                }
                is Resources.Loading -> {
                }
                is Resources.Error -> {
                    requireContext().showToast(response.message.toString())
                }
            }
        })
    }

    private fun setSearchedFriends(friendListData: FriendListData) {

        if (friendListData.count!! <= 0) {
            binding.nofriendslayout.nofreindsLayout.visibility = View.VISIBLE
            binding.friendsListRecycle.visibility = View.GONE
        } else {
            freindsConnectionSearchedList = friendListData.rows

            binding.nofriendslayout.nofreindsLayout.visibility = View.GONE
            binding.friendsListRecycle.visibility = View.VISIBLE
            friendListAdapter.differ.submitList(freindsConnectionSearchedList)
        }


    }

    private fun setFriendsList(friendListData: FriendListData) {
        if (friendListData.count!! <= 0) {
            binding.nofriendslayout.nofreindsLayout.visibility = View.VISIBLE
            binding.friendsListRecycle.visibility = View.GONE
        } else {
            friendListData.rows.forEach { it ->
                if (freindsConnectionList.contains(it)) {

                } else {
                    freindsConnectionList.addAll(listOf(it))
                }
            }
            binding.nofriendslayout.nofreindsLayout.visibility = View.GONE
            binding.friendsListRecycle.visibility = View.VISIBLE
            friendListAdapter.differ.submitList(freindsConnectionList)
        }
    }

    private fun setViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val homeViewModelFactory = HomeViewModelFactory(
            requireActivity().application,
            binjaRepository
        )


        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        val chatViewModelFactory = ChatHistoryViewModelFactory(
            requireActivity().application,
            binjaRepository
        )
        freindsConnectionList.clear()
        chatViewModel =
            ViewModelProvider(this, chatViewModelFactory).get(ChatHistoryViewModel::class.java)

        jsonObject.addProperty("page", 1)
        homeViewModel.FriendsList(requireActivity(), session.token!!, jsonObject)

        val locationProperties = JsonObject()
        locationProperties.addProperty("latitude", session.latitude)
        locationProperties.addProperty("longitude", session.longitude)
        homeViewModel.UserLocation(requireActivity(), session.token!!, locationProperties)

        chatViewModel.ChatCount(requireActivity())
        setObserver()

    }


        private val homeFriendWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchValue = s.toString()
                if (count == 0) {
                    friendListAdapter.differ.submitList(freindsConnectionList)
                } else {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("page", 1)
                    jsonObject.addProperty("search", searchValue)
                    homeViewModel.FriendsList(requireActivity(), session.token!!, jsonObject)
                }

            }
        }

    private fun setRecylerView() {
        binding.friendsListRecycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendListAdapter
            addOnScrollListener(scrollListner)
            friendListAdapter.notifyDataSetChanged()
        }
        val recycleLayoutManager = binding.friendsListRecycle.layoutManager as LinearLayoutManager
        firstVisibleInListview = recycleLayoutManager.findFirstVisibleItemPosition();

    }

    var isLoading = false
    var isScrolling = false


    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val recycleLayoutManager =
                binding.friendsListRecycle.layoutManager as LinearLayoutManager
            if (dy > 0) {
                if (!isLoading) {
                    if (recycleLayoutManager.findLastCompletelyVisibleItemPosition() == friendListAdapter.itemCount - 1) {
                        binding.friendsListRecycle.scrollToPosition(friendListAdapter.itemCount - 1)
                        pageNo++
                        jsonObject.addProperty("page", pageNo)
                        jsonObject.addProperty("search", searchValue)
                        homeViewModel.FriendsList(
                            requireActivity(),
                            session.token!!,
                            jsonObject
                        )
                        isLoading = true
                    }
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true

            }

        }
    }

    fun dptoPx(dp: Int): Int = (dp * requireContext().resources.displayMetrics.density).toInt()

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.movieSearch -> {
                hideKeyboard(requireActivity())
                findNavController().navigate(R.id.fragmentMovieSearch)


            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun GetLocation() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                val locations = p0?.locations
                if (locations != null) {
                    session.latitude = locations[0].latitude.toString()
                    session.longitude = locations[0].longitude.toString()
                }
            }
        }

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location = task.result
                session.latitude = location!!.latitude.toString()
                session.longitude = location.longitude.toString()
            } else {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket!!.off("match_in_progress_listner", OnMatchInProgress)
    }

    override fun onResume() {
        super.onResume()


    }
}