package com.app.fuse.ui.mainScreen.home.OnGoingmatches

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fuse.R
import com.app.fuse.databinding.FragmentOnGoingmatchesFragmnetBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.mainScreen.home.OnGoingmatches.adapter.OnGoingMatchAdapter
import com.app.fuse.ui.mainScreen.home.OnGoingmatches.model.OnGoingMatchList
import com.app.fuse.ui.homescreen.home.HomeViewModel
import com.app.fuse.ui.homescreen.home.HomeViewModelFactory
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.*
import org.json.JSONObject


class OnGoingmatchesFragmnet : Fragment(R.layout.fragment_on_goingmatches_fragmnet) {

    lateinit var binding: FragmentOnGoingmatchesFragmnetBinding
    lateinit var session: SessionManager
    var onGoingMatchAdapter = OnGoingMatchAdapter()
    lateinit var homeViewModel: HomeViewModel
    var onGoingMatchesList: List<OnGoingMatchList> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnGoingmatchesFragmnetBinding.bind(view)

        init()
        setOnClickListner()
        setViewModel()
        setRecyclerView()

    }

    private fun init() {
        session = SessionManager(requireContext())
    }

    private fun setOnClickListner() {

        onGoingMatchAdapter.setOnStartMatchListner { onGoingMatchedata ->
            val filter = onGoingMatchedata.match_games_result.filter
            try {

                val obj = JSONObject(filter)
                val genreFilter:String = obj.getString("movies_type")
                val channelFilter:String = obj.getString("movie_channel")

                val genreArray = ArrayList<String>()
                genreArray.add(genreFilter)

                val channelArray = ArrayList<String>()
                channelArray.add(channelFilter)

                session.channelfilters = channelArray.toSet()
                session.genrefilters = genreArray.toSet()


                val bundle = Bundle()
                bundle.putInt("friendId", onGoingMatchedata.match_games_result.to_user_id)
                bundle.putString("from", "onGoing")
                findNavController().navigate(R.id.movieFragment, bundle)

            } catch (t: Throwable) {
                Log.e("My App", "Could not parse malformed JSON: \"$t\"")
            }


        }

         onGoingMatchAdapter.setOnMatchWithYouListner { onGoingMatchedata ->
            val filter = onGoingMatchedata.match_games_result.filter
            try {

                val obj = JSONObject(filter)
                val genreFilter: String = obj.getString("movies_type")
                val channelFilter: String = obj.getString("movie_channel")

                val genreArray = ArrayList<String>()
                genreArray.add(genreFilter)

                val channelArray = ArrayList<String>()
                channelArray.add(channelFilter)

                session.channelfilters = channelArray.toSet()
                session.genrefilters = genreArray.toSet()

                val bundle = Bundle()
                bundle.putInt("friendId", onGoingMatchedata.match_games_result.from_user_id)
                bundle.putInt("gameId", onGoingMatchedata.match_games_result.id)
                bundle.putString("from", "onGoing")
                findNavController().navigate(R.id.movieFragment, bundle)

            } catch (t: Throwable) {
                Log.e("My App", "Could not parse malformed JSON: \"$t\"")
            }


        }


    }

    private fun setViewModel() {
        var repository = BinjaRepository(BinjaDatabase(requireContext()))
        val homeViewModelFactory = HomeViewModelFactory(requireActivity().application, repository)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        homeViewModel.GetOnGoingMatch(activity!!)

        setObserver()
    }

    private fun setObserver() {
        homeViewModel.onGoingMatch.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    if (response.data!!.status) {
                        setOnGoingMatchData(response.data.data)
                    } else {
                        requireContext().showToast(response.message.toString())
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

    private fun setOnGoingMatchData(onGoingMatchList: List<OnGoingMatchList>) {
        if (onGoingMatchList.isEmpty()) {
            binding.noMatchesGoingOn.makeVisible()
            binding.onGoingMatchRV.makeGone()
        } else {
            onGoingMatchesList = onGoingMatchList
            binding.noMatchesGoingOn.makeGone()
            binding.onGoingMatchRV.makeVisible()
            onGoingMatchAdapter.differ.submitList(onGoingMatchList)
        }

    }

    private fun setRecyclerView() {
        binding.onGoingMatchRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = onGoingMatchAdapter
            onGoingMatchAdapter.notifyDataSetChanged()

        }
    }
}