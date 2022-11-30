package com.app.fuse.ui.Filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fuse.R
import com.app.fuse.databinding.FragmentFilterSelectionBinding
import com.app.fuse.utils.SessionManager
import com.app.fuse.ui.Filter.adapter.ChannelFilterAdapter
import com.app.fuse.ui.Filter.adapter.GenreFilterAdapter
import com.app.fuse.ui.Filter.model.ChannelData
import com.app.fuse.ui.Filter.model.GenreData
import java.io.Serializable


class FilterSelection : Fragment(R.layout.fragment_filter_selection), View.OnClickListener {

    lateinit var binding: FragmentFilterSelectionBinding
    lateinit var session: SessionManager

    var channelIDs: ArrayList<Int> = ArrayList()
    var genreIDs: ArrayList<Int> = ArrayList()

    var channelDataList: List<ChannelData> = ArrayList()
    var genreDataList: List<GenreData> = ArrayList()

    var channelAdapter = ChannelFilterAdapter()
    var genreAdapter = GenreFilterAdapter()

    var type = ""
    var friendID = 0
    val tempChannelIDs = ArrayList<Int>()
    val tempGenreIDs = ArrayList<Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFilterSelectionBinding.bind(view)

        init()
        setFilters()
        setRecycle()
        setClickListner()
    }

    private fun setFilters() {
        type = arguments?.getString("type")!!
        friendID = arguments?.getInt("friendID")!!
        genreDataList = arguments?.getSerializable("genreData")!! as List<GenreData>
        channelDataList = arguments?.getSerializable("channelData")!! as List<ChannelData>

        if (type == "channel") {

            channelDataList.forEach {
                tempChannelIDs.add(it.id)
            }
            binding.selectAll.isChecked = tempChannelIDs.size == channelIDs.size

            channelAdapter.differ.submitList(channelDataList)
            binding.filterListRv.adapter = channelAdapter
            channelAdapter.notifyDataSetChanged()
        } else if (type == "genre") {
            genreDataList.forEach {
                tempGenreIDs.add(it.id)
            }
            binding.selectAll.isChecked = tempGenreIDs.size ==genreIDs.size

            genreAdapter.differ.submitList(genreDataList)
            binding.filterListRv.adapter = genreAdapter
            genreAdapter.notifyDataSetChanged()
        }



        binding.selectAll.setOnClickListener {
            if (binding.selectAll.isChecked) {
                if (type == "channel") {
                    channelDataList.forEach {
                        channelIDs.add(it.id)
                    }

                    channelAdapter.SelectAll(channelIDs)
                    val channelIdSet = channelIDs.map { it.toString() }
                    session.channelfilters = channelIdSet.toSet()

                } else if (type == "genre") {
                    genreDataList.forEach {
                        genreIDs.add(it.id)
                    }
                    genreAdapter.SelectAll(genreIDs)
                    val genreIdSet = genreIDs.map { it.toString() }
                    session.genrefilters = genreIdSet.toSet()
                }
            } else {
                if (type == "channel") {
                    channelIDs.removeAll(channelIDs)
                    channelAdapter.SelectAll(channelIDs)
                    val channelIdSet = channelIDs.map { it.toString() }
                    session.channelfilters = channelIdSet.toSet()

                } else if (type == "genre") {
                    genreIDs.removeAll(genreIDs)
                    genreAdapter.SelectAll(genreIDs)
                    val genreIdSet = genreIDs.map { it.toString() }
                    session.genrefilters = genreIdSet.toSet()
                }
            }
        }
    }

    private fun setRecycle() {
        binding.filterListRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun init() {
        session = SessionManager(requireContext())
        channelIDs = session.channelfilters!!.map { it.toInt() } as ArrayList<Int>
        genreIDs = session.genrefilters!!.map { it.toInt() } as ArrayList<Int>


        channelAdapter.setOnItemClickListener { channelId ->
            binding.selectAll.isChecked =channelId.size == tempChannelIDs.size
            val channelIdSet = channelId.map { it.toString() }
            session.channelfilters = channelIdSet.toSet()


        }

        genreAdapter.setOnItemClickListener { genreId ->
            binding.selectAll.isChecked =genreId.size == tempGenreIDs.size
            println(genreId)
            val genreIdSet =genreId.map { it.toString() }
            session.genrefilters = genreIdSet.toSet()
        }
    }

    private fun setClickListner() {
        binding.submit.setOnClickListener(this)
        binding.backArrow.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.submit -> {
                val bundle = Bundle()
                bundle.putString("from", "FilterSelection")
                bundle.putInt("friendID", friendID)
                bundle.putSerializable("channelData", channelDataList as Serializable)
                bundle.putSerializable("genreData", genreDataList as Serializable)
                findNavController().navigate(R.id.action_filterSelection_to_filterFragment, bundle)
            }
            R.id.backArrow -> {

                findNavController().navigateUp()
            }
        }
    }
}