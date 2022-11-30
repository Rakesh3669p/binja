package com.app.fuse.ui.movieswipe

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.app.fuse.R
import com.app.fuse.databinding.FragmentMovieBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.movieswipe.adapter.MovieAdapter
import com.app.fuse.ui.movieswipe.model.MovieData
import com.app.fuse.ui.movieswipe.model.MovieList
import com.app.fuse.ui.movieswipe.model.MovieVotingData
import com.app.fuse.utils.Constants
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.SocketManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.gson.JsonObject
import com.yuyakaido.android.cardstackview.*
import io.socket.client.Socket


class MovieFragment : Fragment(R.layout.fragment_movie), View.OnClickListener {
    lateinit var binding: FragmentMovieBinding
    lateinit var session: SessionManager
    lateinit var cardStackLayoutManager: CardStackLayoutManager

    lateinit var movieViewModel: MovieViewModel

    private var mSocket: Socket? = null
    var onGoingMatches: Boolean = false
    var movieAdapter: MovieAdapter = MovieAdapter()
    var index = 0
    val movieList: MutableList<MovieList> = ArrayList()
    var swipe = ""
    var pageNo = 1
    var count = 0
    var friendID = 0
    var movieTypeList = ""
    var movieChannelList = ""
    val jsonObject = JsonObject()
    val movieFilterJson = JsonObject()
    var swipedIndex = 0
    var from = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieBinding.bind(view)
        init()
        setOnBackPress()
        SocketConnection()
        setBannerAd()
        setViewModel()
        setClickListners()
        setMovieStack()
        setRecycle()
    }

    private fun init() {
        session = SessionManager(requireContext())
        friendID = arguments?.getInt("friendId")!!
        from = arguments?.getString("from")!!
    }


    private fun SocketConnection() {
        SocketManager.instance!!.connectSocket(session.token!!, Constants.BASE_URL)
        mSocket = SocketManager.instance!!.getSocket()
        mSocket!!.connect()

        if (mSocket!!.connected()) {
            Log.d("TAG", "Socket Connected :)")
        } else {
            Log.d("TAG", "Socket Not Connected:(")
//            requireContext().showToast("Oops!! Something Went try again later")
        }
    }

    private fun setBannerAd() {
        MobileAds.initialize(requireContext()) {}
        AdView(requireContext()).apply {
            adSize = AdSize.BANNER
            adUnitId = getString(R.string.banner_ad_key)
        }
        val adRequest = AdRequest.Builder().build()
        binding.bannerAd.loadAd(adRequest)
    }


    private fun setViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val movieViewModelFactory = MovieViewModelFactory(requireActivity().application, binjaRepository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        checkOnGoingMatchesData()

    }

    private fun checkOnGoingMatchesData() {
        movieViewModel.getOnGoingMatchesFriends().observe(
            viewLifecycleOwner, Observer { friendsDetails ->
                friendsDetails.forEach {
                    if (it.id == friendID) {
                        onGoingMatches = true
                    }
                }
                apiCall()
                if (from == "game") {

                } else {
                    setMatchInProgress()
                }
            })

    }

    private fun apiCall() {

        if (from != "home") {
            movieTypeList = session.genrefilters.toString()
            movieChannelList = session.channelfilters.toString()
        } else if (from == "home") {
            val movieType = session.genrefilters!!.map { it.toInt() }
            val movieChannel = session.channelfilters!!.map { it.toInt() }
            movieTypeList = TextUtils.join(", ", movieType)
            movieChannelList = TextUtils.join(", ", movieChannel)
        }

        movieFilterJson.addProperty("movies_type", session.genrefilters.toString())
        movieFilterJson.addProperty("movie_channel", session.channelfilters.toString())

        jsonObject.addProperty("page", pageNo)
        jsonObject.addProperty("movies_type", movieTypeList)
        jsonObject.addProperty("movie_channel", movieChannelList)

        if (from == "game"||from=="onGoing") {
            jsonObject.addProperty("match_user_id", friendID)
        } else {
            jsonObject.addProperty("friend_user_id", friendID)
            if (onGoingMatches) {
                jsonObject.addProperty("on_going_match", 1)
            }
        }

        jsonObject.addProperty("search", "")
        movieViewModel.MovieList(requireActivity(), jsonObject)
        setObserver()
    }


    private fun setObserver() {
        movieViewModel.movieData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data!!.status) {
                        binding.progressBar.pgrBarLayout.hide(requireActivity())
                        setMovieData(response.data.data)
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

        movieViewModel.movieVoting.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data!!.status) {
                        if (from == "home") {

                        } else {
                            setMovieVoting(response.data.data)
                        }
                    } else {
                        requireContext().showToast(response.data.msg)
                    }
                }
                is Resources.Loading -> {

                }
                is Resources.Error -> {
                    requireContext().showToast(response.message.toString())
                }
            }
        })


        movieViewModel.gameRequestDelete.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    if(response.data?.status!!){
                        showGameOverDialogue()
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


    private fun setMatchInProgress() {

        val matchInProgressParams = JsonObject()
        matchInProgressParams.addProperty("from_user_id", session.userID)
        matchInProgressParams.addProperty("to_user_id", friendID)
        matchInProgressParams.addProperty("filter", movieFilterJson.toString())
        matchInProgressParams.addProperty("notication", from == "game")

        movieViewModel.MatchInProgress(requireActivity(), matchInProgressParams)
        movieViewModel.matchInProgress.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                }
                is Resources.Loading -> {
                }
                is Resources.Error -> {
                }
            }
        })
    }


    private fun setMovieVoting(movieVotingData: MovieVotingData) {
        if (movieVotingData.is_matched) {
            binding.movieMatched.root.visibility = View.VISIBLE
            binding.popcornLayout.root.visibility = View.VISIBLE
            binding.movieListStack.visibility = View.GONE

            Glide.with(requireContext()).load("${movieList[index].movie_image!!}")
                .into(binding.movieMatched.movieMatchedImage)

            Glide.with(requireContext()).load(R.drawable.popcorn_matched)
                .into(binding.popcornLayout.popcorn)

            binding.movieMatched.movieMatchedTitle.text = movieList[index].movie_name
            binding.movieMatched.movieMatchedRating.text =
                "rating: ${movieList[index].movie_rating}"
            binding.movieMatched.movieMatchedDesc.text = movieList[index].movie_description

            binding.popcornLayout.movieMatchedContinue.setOnClickListener {
                binding.movieListStack.visibility = View.VISIBLE
                binding.movieMatched.root.visibility = View.GONE
                binding.popcornLayout.root.visibility = View.GONE
            }
        }
    }

    private fun setMovieData(movieData: MovieData) {

        count = movieData.count
        when (count) {
            0 -> {
                binding.noMatchesFound.visibility = View.VISIBLE
            }
            else -> {
                binding.noMatchesFound.visibility = View.GONE

                movieData.rows.forEach { it ->
                    if (movieList.contains(it)) {
                    } else {
                        movieList.addAll(listOf(it))
                    }
                }

                movieAdapter.differ.submitList(movieList)
                movieAdapter.notifyItemRangeInserted(movieAdapter.itemCount - 1, movieList.size)

            }
        }

    }

    private fun setClickListners() {
        binding.shareMovieStack.setOnClickListener(this)
        binding.popcornLayout.movieMatchedContinue.setOnClickListener(this)
        binding.backMovieStack.setOnClickListener(this)
        binding.searchMovieStack.setOnClickListener(this)
    }


    private fun setMovieStack() {

        cardStackLayoutManager = CardStackLayoutManager(requireContext(),
            object : CardStackListener {
                override fun onCardDragging(direction: Direction?, ratio: Float) {
                    binding.popcornLayout.root.visibility = View.INVISIBLE
                }

                override fun onCardSwiped(direction: Direction?) {
                    Log.d("CardDirections ", direction.toString())
                    when (direction) {
                        Direction.Right -> {
                            swipe = "R"
                            movieVote(1)
                        }
                        Direction.Left -> {
                            swipe = "L"
                            movieVote(0)
                        }
                        Direction.Top -> {
                            swipe = "T"
                            movieVote(1)
                        }
                        Direction.Bottom -> {
                            swipe = "B"
                            movieVote(0)
                        }
                    }
                }

                override fun onCardRewound() {
                    Log.d("CardAppeared TAG", "REWONED")

                }

                override fun onCardCanceled() {
                    Log.d("CardAppeared TAG", "CANCELED")


                }

                override fun onCardAppeared(view: View?, position: Int) {

                }

                override fun onCardDisappeared(view: View?, position: Int) {
                    swipedIndex = position
                    if (count == position + 1) {
                        binding.noMoreMovielbl.visibility = View.VISIBLE
                        if (from == "game"||from == "onGoing") {
                            movieViewModel.DeleteGameRequests(
                                activity!!,
                                arguments?.getInt("gameId")!!
                            )
                        }

                    } else {
                        binding.noMoreMovielbl.visibility = View.GONE
                    }
                    Log.d("TAG", "Count: $position ${movieList.size - 2} ${count}")

                    if (movieList.size - 1 == position) {
                        if (movieList.size != count) {
                            pageNo++
                            jsonObject.addProperty("page", pageNo)
                            jsonObject.addProperty("movies_type", movieTypeList)
                            jsonObject.addProperty("movie_channel", movieChannelList)
                            jsonObject.addProperty("search", "")

                            movieViewModel.MovieList(
                                requireActivity(),
                                jsonObject
                            )
                        }

                    }
                }
            })

        cardStackLayoutManager.setStackFrom(StackFrom.Right)
        cardStackLayoutManager.setVisibleCount(3)
        cardStackLayoutManager.setTranslationInterval(8.0f)
        cardStackLayoutManager.setScaleInterval(0.95f)
        cardStackLayoutManager.setSwipeThreshold(0.3f)
        cardStackLayoutManager.setMaxDegree(20.0f)
        cardStackLayoutManager.setDirections(Direction.HORIZONTAL)
        cardStackLayoutManager.setCanScrollHorizontal(true)
        cardStackLayoutManager.setCanScrollVertical(true)
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        cardStackLayoutManager.setOverlayInterpolator(LinearInterpolator())
    }

    private fun setRecycle() {
        binding.movieListStack.apply {
            setHasFixedSize(true)
            layoutManager = cardStackLayoutManager
            adapter = movieAdapter
            itemAnimator = DefaultItemAnimator()
        }
    }

    private fun movieVote(vote: Int) {

        index = swipedIndex
        val jsonObject = JsonObject()
        jsonObject.addProperty("movie_id", movieList[swipedIndex].id)
        jsonObject.addProperty("vote", vote)
        jsonObject.addProperty("match_user_id", friendID)
        jsonObject.addProperty("is_send_notification", from != "home")
        println("onGoingMatches $jsonObject")
        movieViewModel.MovieVoting(
            requireActivity(),

            jsonObject
        )
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.shareMovieStack -> {
                rewindCard()
            }
            R.id.movieMatchedContinue -> {
                binding.movieMatched.root.visibility = View.GONE
            }
            R.id.backMovieStack -> {
                OnBackPressed()

            }
            R.id.searchMovieStack -> {
                findNavController().navigate(R.id.fragmentMovieSearch)
            }


        }
    }

    fun rewindCard() {
        val setting = RewindAnimationSetting.Builder()
            .setDirection(Direction.Bottom)
            .setDuration(Duration.Normal.duration)
            .setInterpolator(DecelerateInterpolator())
            .build()
        cardStackLayoutManager.setRewindAnimationSetting(setting)
        binding.movieListStack.rewind()
    }

    private fun setOnBackPress() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    OnBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    private fun OnBackPressed() {

        if (from == "game") {
            session.isFromGameRequestScreen = true
        }

        findNavController().navigateUp()
    }

    private fun showGameOverDialogue() {

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.game_over_dialog)
        val yesBtn = dialog.findViewById(R.id.okbutton) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

        /*val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()

        alert.setTitle("Game Over")
        alert.show()
        val positiveButton: Button =
            alert.getButton(AlertDialog.BUTTON_POSITIVE)
        val positiveButtonLL: LinearLayout.LayoutParams =
            positiveButton.layoutParams as LinearLayout.LayoutParams
        positiveButtonLL.gravity = Gravity.CENTER
        positiveButton.layoutParams = positiveButtonLL*/
    }


    override fun onPause() {
        OnBackPressed()
        super.onPause()
    }

    override fun onStop() {
        OnBackPressed()
        super.onStop()
    }
}
