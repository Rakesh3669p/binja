package com.app.fuse.ui.friendsMoviematched

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fuse.R
import com.app.fuse.databinding.FragmentFriendsMovieMatchedBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.app.fuse.ui.friendsMoviematched.adapter.FriendsMovieMatchedAdapter
import com.app.fuse.ui.friendsMoviematched.model.FriendsMovieMatchedData
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.gson.JsonObject


class FriendsMovieMatched : Fragment(R.layout.fragment_friends_movie_matched),
    View.OnClickListener {

    lateinit var binding: FragmentFriendsMovieMatchedBinding
    lateinit var session: SessionManager
    lateinit var friendsMovieMatchedViewModel: FriendsMovieMatchedViewModel
    var friendsMovieMatchedAdapter: FriendsMovieMatchedAdapter = FriendsMovieMatchedAdapter()
    var friendsMovieMatchedList: MutableList<FriendsMovieMatchedData> = ArrayList()
    var movieID = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFriendsMovieMatchedBinding.bind(view)
        init()
        setRecylce()
        setBannerAd()

        setViewModel()
        setOnClickListner()
    }

    private fun setOnClickListner() {
        binding.backArrow.setOnClickListener(this)
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


    private fun init() {
        session = SessionManager(requireContext())
        movieID = arguments?.getInt("movieID")!!
    }

    private fun setViewModel() {
        val repository = BinjaRepository(BinjaDatabase(requireContext()))
        val friendsMovieMatchedViewModelFactory =
            FriendsMovieMatchedViewModelFactory(requireActivity().application, repository)
        friendsMovieMatchedViewModel =
            ViewModelProvider(this, friendsMovieMatchedViewModelFactory).get(
                FriendsMovieMatchedViewModel::class.java
            )

        val jsonObject = JsonObject()
        jsonObject.addProperty("movie_id", movieID)
        friendsMovieMatchedViewModel.FriendsMovieMatched(
            requireActivity(),
            session.token!!,
            jsonObject
        )

        setObserver()
    }

    private fun setObserver() {
        friendsMovieMatchedViewModel.friendsMovieMatched.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resources.Success -> {
                        binding.progressBar.pgrBarLayout.hide(requireActivity())
                        if (response.data!!.status) {
                            if(response.data.data.isEmpty()) {
                                binding.nofirendstoshow.visibility = View.VISIBLE
                                binding.friendsMatchedRv.visibility = View.GONE
                            }else{
                                binding.nofirendstoshow.visibility = View.GONE
                                binding.friendsMatchedRv.visibility = View.VISIBLE
                                setData(response.data.data)
                            }

                        } else {
                            requireContext().showToast(response.data.msg)
                        }

                    }
                    is Resources.Loading -> {
                        binding.progressBar.pgrBarLayout.show(requireActivity())
                    }
                    is Resources.Error -> {
                        binding.nofirendstoshow.visibility = View.VISIBLE
                        binding.friendsMatchedRv.visibility = View.GONE
                        binding.progressBar.pgrBarLayout.hide(requireActivity())
                        requireContext().showToast(response.message.toString())
                    }
                }

            })
    }

    private fun setData(friendsdata: List<FriendsMovieMatchedData>) {

        friendsdata.forEach { it ->
            if (friendsMovieMatchedList.contains(it)) {
            } else {
                friendsMovieMatchedList.addAll(listOf(it))
            }
        }

        friendsMovieMatchedAdapter.differ.submitList(friendsMovieMatchedList)
        friendsMovieMatchedAdapter.notifyDataSetChanged()

    }

    private fun setRecylce() {
        binding.friendsMatchedRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendsMovieMatchedAdapter
            friendsMovieMatchedAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backArrow -> {
                findNavController().navigateUp()
            }
        }
    }


}