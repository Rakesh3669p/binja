package com.app.fuse.ui.Filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.fuse.R
import com.app.fuse.databinding.FragmentChannelSelectionBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.Filter.adapter.ChannelFilterAdapter
import com.app.fuse.ui.Filter.model.ChannelData
import com.app.fuse.ui.Filter.model.GenreData
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.google.android.material.chip.Chip
import java.io.Serializable


@Suppress("NAME_SHADOWING")
class Filter : Fragment(R.layout.fragment_channel_selection), View.OnClickListener {
    lateinit var binding: FragmentChannelSelectionBinding
    lateinit var session: SessionManager
    lateinit var filterViewModel: FilterViewModel

    var channelDataList: List<ChannelData> = ArrayList()

    var channelIDs: ArrayList<Int> = ArrayList()
    var channelAdapter: ChannelFilterAdapter = ChannelFilterAdapter()

    var genreDataList: List<GenreData> = ArrayList()
    var genreIDs: ArrayList<Int> = ArrayList()

    var friendUserId = 0
    var from = ""
    var onGoingMatch = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChannelSelectionBinding.bind(view)
        init()
        setViewModel()
        setOnClickListners()

    }

    private fun setChips() {
        if (channelIDs.isNotEmpty()) {
            channelDataList.forEach { it ->

                if (channelIDs.contains(it.id)) {
                    val chip = LayoutInflater.from(activity).inflate(R.layout.chip, null) as Chip
                    chip.id = it.id
                    binding.movieChipGroup.removeView(chip)
                    chip.text = it.subscribe_channel_name
                    binding.movieChipGroup.addView(chip)

                    chip.setOnClickListener { chip ->
                        binding.movieChipGroup.removeView(chip)
                        channelIDs.remove(chip.id)
                        val channelIdSet =
                            channelIDs.map { channelIds -> channelIds.toString() }
                        session.channelfilters = channelIdSet.toSet()
                        if (channelIDs.isNotEmpty() && genreIDs.isNotEmpty()) {
                            binding.letStartCV.visibility = View.VISIBLE
                            binding.onGo.visibility = View.VISIBLE
                        } else {
                            binding.letStartCV.visibility = View.GONE
                            binding.onGo.visibility = View.GONE
                        }
                    }
                }
            }
        }

        if (genreIDs.isNotEmpty()) {
            genreDataList.forEach {
                if (genreIDs.contains(it.id)) {
                    val chip = LayoutInflater.from(activity).inflate(R.layout.chip, null) as Chip
                    chip.id = it.id
                    binding.movieChipGroup.removeView(chip)
                    chip.text = it.movies_type_name
                    binding.genreChipGroup.addView(chip)
                    chip.setOnClickListener { chip ->
                        binding.genreChipGroup.removeView(chip)
                        genreIDs.remove(chip.id)
                        val genreIdSet = genreIDs.map { genreIds -> genreIds.toString() }
                        session.genrefilters = genreIdSet.toSet()
                        if (channelIDs.isNotEmpty() && genreIDs.isNotEmpty()) {
                            binding.letStartCV.visibility = View.VISIBLE
                            binding.onGo.visibility = View.VISIBLE
                        } else {
                            binding.letStartCV.visibility = View.GONE
                            binding.onGo.visibility = View.GONE
                        }
                    }
                }
            }
        }
        binding.filterLayout.visibility = View.VISIBLE
    }

    private fun init() {
        session = SessionManager(requireContext())
        channelAdapter = ChannelFilterAdapter()
        friendUserId = arguments?.getInt("friendID")!!
        from = arguments?.getString("from").toString()
        onGoingMatch = arguments?.getInt("onGoingMatch")!!
        val channelSelected = session.channelfilters
        val genreSelected = session.genrefilters

        channelSelected!!.forEach {
            channelIDs.add(it.toInt())
        }
        genreSelected!!.forEach {
            genreIDs.add(it.toInt())
        }

        if (channelIDs.isNotEmpty() && genreIDs.isNotEmpty()) {
            binding.letStartCV.visibility = View.VISIBLE
            binding.onGo.visibility = View.VISIBLE
        } else {
            binding.letStartCV.visibility = View.GONE
            binding.onGo.visibility = View.GONE
        }

        val bundle = Bundle()
        binding.edtSearchChannel.setOnClickListener {
            bundle.putSerializable("friendID", friendUserId)
            bundle.putSerializable("type", "channel")
            bundle.putSerializable("channelData", channelDataList as Serializable)
            bundle.putSerializable("genreData", genreDataList as Serializable)
            findNavController().navigate(R.id.fragmentfilterSelection, bundle)
        }

        binding.edtSearchGenre.setOnClickListener {
            bundle.putSerializable("friendID", friendUserId)
            bundle.putSerializable("type", "genre")
            bundle.putSerializable("channelData", channelDataList as Serializable)
            bundle.putSerializable("genreData", genreDataList as Serializable)
            findNavController().navigate(R.id.fragmentfilterSelection, bundle)
        }

    }


    private fun setViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val filterViewModelFactory =
            FilterViewModelFactory(requireActivity().application, binjaRepository)
        filterViewModel =
            ViewModelProvider(this, filterViewModelFactory).get(FilterViewModel::class.java)

        if (arguments?.getString("from") == "Home") {
            filterViewModel.ChannelList(requireActivity(), session.token!!)
            filterViewModel.GenreList(requireActivity(), session.token!!)
            setChannelAndGenreObserver()
        } else if (arguments?.getString("from") == "FilterSelection") {
            channelDataList = arguments?.getSerializable("channelData")!! as List<ChannelData>
            genreDataList = arguments?.getSerializable("genreData")!! as List<GenreData>
            setChips()
        }

    }


    private fun setChannelAndGenreObserver() {
        filterViewModel.channelData.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {

                    if (response.data!!.status) {
                        setChannelData(response.data.data)
                    }

                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                    binding.filterLayout.visibility = View.INVISIBLE
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    binding.filterLayout.visibility = View.INVISIBLE
                }
            }

        })

        filterViewModel.genreData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data!!.status) {
                        setGenreData(response.data.data)
                    }
                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                    binding.filterLayout.visibility = View.INVISIBLE

                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    binding.filterLayout.visibility = View.INVISIBLE

                }
            }

        })
    }

    private fun setGenreData(data: List<GenreData>) {
        genreDataList = data
        if (genreIDs.isNotEmpty()) {
            genreDataList.forEach { it ->
                if (genreIDs.contains(it.id)) {
                    val chip = LayoutInflater.from(activity).inflate(R.layout.chip, null) as Chip
                    chip.id = it.id
                    binding.movieChipGroup.removeView(chip)
                    chip.text = it.movies_type_name
                    binding.genreChipGroup.addView(chip)
                    chip.setOnClickListener { chip ->
                        binding.genreChipGroup.removeView(chip)
                        genreIDs.remove(chip.id)
                        val genreIdSet = genreIDs.map { genreIds -> genreIds.toString() }
                        session.genrefilters = genreIdSet.toSet()
                        if (channelIDs.isNotEmpty() && genreIDs.isNotEmpty()) {
                            binding.letStartCV.visibility = View.VISIBLE
                            binding.onGo.visibility = View.VISIBLE
                        } else {
                            binding.letStartCV.visibility = View.GONE
                            binding.onGo.visibility = View.GONE
                        }
                    }
                }
            }
        }
        binding.progressBar.pgrBarLayout.hide(requireActivity())


    }

    private fun setChannelData(data: List<ChannelData>) {
        channelDataList = data
        channelDataList.forEach { it ->
            if (channelIDs.contains(it.id)) {
                val chip =
                    LayoutInflater.from(activity).inflate(R.layout.chip, null) as Chip
                chip.id = it.id
                binding.movieChipGroup.removeView(chip)
                chip.text = it.subscribe_channel_name
                binding.movieChipGroup.addView(chip)

                chip.setOnClickListener { chip ->
                    binding.movieChipGroup.removeView(chip)
                    channelIDs.remove(chip.id)
                    val channelIdSet =
                        channelIDs.map { channelIds -> channelIds.toString() }
                    session.channelfilters = channelIdSet.toSet()
                    if (channelIDs.isNotEmpty() && genreIDs.isNotEmpty()) {
                        binding.letStartCV.visibility = View.VISIBLE
                        binding.onGo.visibility = View.VISIBLE
                    } else {
                        binding.letStartCV.visibility = View.GONE
                        binding.onGo.visibility = View.GONE
                    }
                }
            }
        }
        binding.filterLayout.visibility = View.VISIBLE
        binding.progressBar.pgrBarLayout.hide(requireActivity())

    }


    private fun setOnClickListners() {
        binding.letStartCV.setOnClickListener(this)
        binding.onGo.setOnClickListener(this)
        binding.backArrow.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.letStartCV -> {
                saveFilters()
            }
            R.id.onGo -> {
                saveFilters()
            }
            R.id.backArrow -> {
                findNavController().navigateUp()
            }
        }
    }

    private fun saveFilters() {


        if (session.genrefilters!!.isNotEmpty() && session.channelfilters!!.isNotEmpty()) {
            val bundle = Bundle()
            bundle.putInt("friendId", friendUserId)
            bundle.putInt("userId", session.userID!!)
            bundle.putString("from", "home")
            bundle.putInt("onGoingMatch", onGoingMatch)
            findNavController().navigate(R.id.action_filterFragment_to_movieFragment, bundle)
        } else {

            if (session.genrefilters!!.isEmpty()) {
                requireContext().showToast("Please Select Movie Type")
            }

            if (session.channelfilters!!.isEmpty()) {
                requireContext().showToast("Please Select Subscription Channel")
            }
        }
    }
}