package com.app.fuse.ui.moviematchedlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.FragmentMovieMatchedListBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.app.fuse.ui.moviematchedlist.adapter.MovieMatchedAdapter
import com.app.fuse.ui.moviematchedlist.model.MovieList
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.gson.JsonObject


class MovieMatchedList : Fragment(R.layout.fragment_movie_matched_list), View.OnClickListener {
    lateinit var binding: FragmentMovieMatchedListBinding
    lateinit var session: SessionManager
    lateinit var movieMatchedViewModel: MovieMatchedViewModel

    private var movieMatchedAdapter: MovieMatchedAdapter = MovieMatchedAdapter()
    private var movieMatchedData: MutableList<MovieList> = ArrayList()
    val jsonObject = JsonObject()

    var friendUserID = 0
    var pageNo = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieMatchedListBinding.bind(view)

        init()
        setBannerAd()

        setOnClickListners()
        setViewModel()
        setRecycleView()
    }

    private fun setOnClickListners() {
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

    private fun setRecycleView() {
        binding.movieMatchedRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = movieMatchedAdapter
            movieMatchedAdapter.notifyDataSetChanged()
        }
    }

    private fun setViewModel() {
        val repository = BinjaRepository(BinjaDatabase(requireContext()))
        val movieMatchedViewModelFactory = MovieMatchedModelFactory(requireActivity().application, repository)

        movieMatchedViewModel = ViewModelProvider(this, movieMatchedViewModelFactory).get(MovieMatchedViewModel::class.java)

        jsonObject.addProperty("user_id", friendUserID)
        movieMatchedViewModel.MovieMatched(requireActivity(), session.token!!, jsonObject)
        setObserver()
    }

    private fun setObserver() {
        movieMatchedViewModel.movieMatched.observe(viewLifecycleOwner, Observer { resposne ->
            when (resposne) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    if (resposne.data!!.status) {
                        if(resposne.data.data.isEmpty()){
                            binding.movieMatchedRv.visibility = View.GONE
                            binding.noMoviesMatched.visibility = View.VISIBLE
                        }else{
                            setMovieMatchedData(resposne.data?.data)
                            binding.movieMatchedRv.visibility = View.VISIBLE
                            binding.noMoviesMatched.visibility = View.GONE
                        }

                    } else {
                        requireContext().showToast(resposne.data.msg)
                        binding.movieMatchedRv.visibility = View.GONE
                        binding.noMoviesMatched.visibility = View.VISIBLE
                    }

                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    requireContext().showToast(resposne.message.toString())
                    binding.noMoviesMatched.visibility = View.VISIBLE
                    binding.movieMatchedRv.visibility = View.VISIBLE
                }

            }

        })
    }

    private fun setMovieMatchedData(movieList: List<MovieList>) {
        movieList.forEach { it ->
            if (movieMatchedData.contains(it)) {
            } else {
                movieMatchedData.addAll(listOf(it))
            }
        }


        movieMatchedAdapter.differ.submitList(movieMatchedData)
        movieMatchedAdapter.notifyDataSetChanged()
    }


    private fun init() {
        session = SessionManager(requireContext())
        friendUserID = arguments?.getInt("friendID")!!
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backArrow -> {
             findNavController().navigateUp()
            }
        }
    }

    var isLoading = false
    var isScrolling = false

    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (dy > 0) {

                    val recycleLayoutManager =
                        binding.movieMatchedRv.layoutManager as GridLayoutManager
                    if (!isLoading) {

                        if (recycleLayoutManager != null && recycleLayoutManager.findLastCompletelyVisibleItemPosition() == movieMatchedAdapter.itemCount - 1) {
                            pageNo++
                            jsonObject.addProperty("user_id", friendUserID)
                            jsonObject.addProperty("page", pageNo)
                            movieMatchedViewModel.MovieMatched(
                                activity!!,
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
}