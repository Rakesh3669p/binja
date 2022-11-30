package com.app.fuse.ui.mainScreen.notification.gameRequests

import android.os.Bundle
import android.util.Log.*
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fuse.R
import com.app.fuse.databinding.FragmentRequestGameBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.showToast
import com.app.fuse.ui.mainScreen.notification.gameRequests.adapter.GameRequestAdapter
import com.app.fuse.ui.mainScreen.notification.gameRequests.model.GameRequestData
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class RequestGameFragment : Fragment(R.layout.fragment_request_game) {

    lateinit var binding: FragmentRequestGameBinding
    lateinit var session: SessionManager
    var gameRequestAdapter: GameRequestAdapter = GameRequestAdapter()
    lateinit var requestGameViewModel: RequestGameViewModel
    var requestGameDataList = ArrayList<GameRequestData>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRequestGameBinding.bind(view)

        init()
        setOnClickListners()
        setViewModel()
        setRecyclerView()

    }


    private fun init() {
        session = SessionManager(requireContext())
    }


    private fun setOnClickListners() {
        gameRequestAdapter.setOnAcceptClickListener { requestGameData ->

            val filter = requestGameData.filter

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

                /* val genreFilter: String = obj.getString("movies_type")
                 val channelFilter: String = obj.getString("movie_channel")

                 val movieTypeArray: JSONArray = JSONArray(genreFilter)
                 val movieChannelArray: JSONArray = JSONArray(channelFilter)
                 val genreArray = ArrayList<String>()
                 val channelArray = ArrayList<String>()

                 for (i in 0 until movieTypeArray.length()){
                     genreArray.add(movieTypeArray[i].toString())
                 }
                 for (i in 0 until movieChannelArray.length()){
                     channelArray.add(movieChannelArray[i].toString())
                 }

 */
                val bundle = Bundle()
                bundle.putInt("friendId", requestGameData.from_users_data.id!!)
                bundle.putString("from", "game")
                bundle.putInt("gameId", requestGameData.id)
                findNavController().navigate(R.id.movieFragment, bundle)

//                requestGameViewModel.DeleteGameRequests(activity!!,requestGameData.id)

            } catch (t: Throwable) {
                e("My App", "Could not parse malformed JSON: \"$t\"")
            }
        }
    }

    private fun setViewModel() {
        val repository = BinjaRepository(BinjaDatabase(requireContext()))
        val gameViewModelFactory =
            RequestGameViewModelFactory(requireActivity().application, repository)
        requestGameViewModel =
            ViewModelProvider(this, gameViewModelFactory).get(RequestGameViewModel::class.java)

        requestGameViewModel.GetGameRequests(requireActivity())
        setObServer()
    }

    private fun setObServer() {
        requestGameViewModel.gameRequest.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    if (response.data!!.status) {
                        setRequestGameData(response.data.data)
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

    private fun setRequestGameData(gameRequestData: List<GameRequestData>) {
        gameRequestData.forEach { it ->
            if (requestGameDataList.contains(it)) {
            } else {
                requestGameDataList.addAll(listOf(it))
            }
        }
        gameRequestAdapter.differ.submitList(requestGameDataList)
        gameRequestAdapter.notifyDataSetChanged()
    }

    private fun setRecyclerView() {
        binding.gameRequestRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = gameRequestAdapter
            gameRequestAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        requestGameDataList.clear()
        super.onResume()
    }
}