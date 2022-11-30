package com.app.fuse.ui.homescreen.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.FragmentSearchBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.homescreen.search.adapter.MainSearchAdapter
import com.app.fuse.ui.homescreen.search.adapter.SearchViewAdapter
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.*

import com.app.fuse.ui.mainScreen.search.model.SearchData
import com.app.fuse.ui.mainScreen.search.model.SearchList
import com.google.gson.JsonObject


class Search : Fragment(R.layout.fragment_search), View.OnClickListener, View.OnTouchListener {
    lateinit var binding: FragmentSearchBinding
    var searchvViewAdapter: SearchViewAdapter = SearchViewAdapter()
    var mainSearchAdapter: MainSearchAdapter = MainSearchAdapter()
    lateinit var session: SessionManager
    lateinit var searchViewModel: SearchViewModel
    var pageNo = 1
    var searchValue = ""
    val jsonObject = JsonObject()
    lateinit var gridLayoutManager: GridLayoutManager
    var searchDataList: MutableList<SearchList> = ArrayList()
    var searchedData: List<SearchList> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        init()
        setOnClickListners()
        searchViewModel()
        setRecycle()
    }


    private fun searchViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val searchViewModelFactory = SearchViewModelFactory(activity!!.application, binjaRepository)
        searchViewModel =
            ViewModelProvider(this, searchViewModelFactory).get(SearchViewModel::class.java)
        jsonObject.addProperty("page", pageNo)
        jsonObject.addProperty("search", searchValue)
        searchViewModel.Search(activity!!, session.token!!, jsonObject)
        setObserver()
    }

    private fun setObserver() {
        searchViewModel.searchData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    Log.d("SearhData...", response.data.toString())

                    response.data.let { searchResponse ->
                        if (searchValue.isNotEmpty()) {
                            if (searchResponse!!.data.rows.isNotEmpty()) {
                                setMainSearchData(response.data!!.data)
                                isLoading = false
                            }
                        } else {

                            if (searchResponse!!.data.rows.isNotEmpty()) {
                                setSearchData(response.data!!.data)
                                isLoading = false
                            }

                        }
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

    private fun setMainSearchData(searchData: SearchData) {
        mainSearchAdapter.differ.submitList(searchData.rows)
        mainSearchAdapter.notifyDataSetChanged()
    }

    private fun setSearchData(searchData: SearchData) {
        searchData.rows.forEach { it ->
            if (searchDataList.contains(it)) {
            } else {
                searchDataList.addAll(listOf(it))
            }
        }


        searchvViewAdapter.differ.submitList(searchDataList)
        searchvViewAdapter.notifyDataSetChanged()

    }

    private fun setOnClickListners() {
        binding.edtSearch.setOnTouchListener(this)
    }


    private fun init() {
        session = SessionManager(requireContext())
        gridLayoutManager = GridLayoutManager(requireContext(), 2)


        mainSearchAdapter.setOnItemClickListener {
            searchViewModel.AddSearchedUser(
                requireActivity(),
                session.token!!,
                it
            )
            hideKeyboard(requireActivity())
            val bundle = Bundle()
            bundle.putInt("userID", it.id!!)
            findNavController().navigate(R.id.singleUserView, bundle)
            binding.mainSearchView.edtSearchMain.text.clear()

        }
        mainSearchAdapter.setOnRequestClickListener {
            sendFriendRequest(it)
        }
        searchvViewAdapter.setOnItemClickListener {
            hideKeyboard(requireActivity())
            val bundle = Bundle()
            bundle.putInt("userID", it.id!!)
            findNavController().navigate(R.id.singleUserView, bundle)
            binding.mainSearchView.edtSearchMain.text.clear()

        }
    }

    private fun sendFriendRequest(userID: Int) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("send_friend_request_id", userID)
        searchViewModel.SendFriendRequest(requireActivity(), session.token!!, jsonObject)

        searchViewModel.sendFriendRequestData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    if(response.data!!.status){
                        jsonObject.addProperty("page", 1)
                        jsonObject.addProperty("search", searchValue)
                        searchViewModel.Search(activity!!, session.token!!, jsonObject)
                        searchViewModel.GetRecentSearchedUser(activity!!, session.token!!)
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


    private fun setRecycle() {
        binding.searchViewListRecycle.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = searchvViewAdapter
            searchvViewAdapter.notifyDataSetChanged()
            addOnScrollListener(this@Search.scrollListner)
        }
    }


    var isLoading = false
    var isScrolling = false


    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (dy > 0) {
                if (binding.searchViewListRecycle.layoutManager != LinearLayoutManager(
                        requireContext()
                    )
                ) {
                    val recycleLayoutManager =
                        binding.searchViewListRecycle.layoutManager as LinearLayoutManager
                    if (!isLoading) {

                        if (recycleLayoutManager != null && recycleLayoutManager.findLastCompletelyVisibleItemPosition() == searchvViewAdapter.itemCount - 1) {
                            pageNo++
                            jsonObject.addProperty("search", searchValue)
                            jsonObject.addProperty("page", pageNo)
                            searchViewModel.Search(
                                activity!!,
                                session.token!!,
                                jsonObject
                            )
                            isLoading = true
                        }

                    }
                } else {
                    val recycleLayoutManager =
                        binding.searchViewListRecycle.layoutManager as GridLayoutManager
                    if (!isLoading) {

                        if (recycleLayoutManager != null && recycleLayoutManager.findLastCompletelyVisibleItemPosition() == searchvViewAdapter.itemCount - 1) {
                            pageNo++
                            jsonObject.addProperty("search", searchValue)
                            jsonObject.addProperty("page", pageNo)
                            searchViewModel.Search(
                                activity!!,
                                session.token!!,
                                jsonObject
                            )
                            isLoading = true
                        }

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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backBtn -> {
                hideKeyboard(activity!!)
                binding.mainSearchView.edtSearchMain.text.clear()
                binding.mainSearchView.edtSearchMain.clearFocus()
                binding.mainSearchView.root.visibility = View.INVISIBLE
                binding.edtSearch.visibility = View.VISIBLE

                binding.searchListlbl.text = getString(R.string.searchListlbl)
                setRecycle()
            }

        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v!!.id) {
            R.id.edtSearch -> {
                setMainSearchView()
            }

        }
        return false
    }

    private fun setMainSearchView() {
        binding.searchViewListRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.searchViewListRecycle.adapter = mainSearchAdapter
        binding.mainSearchView.backBtn.setOnClickListener(this)
        binding.mainSearchView.root.visibility = View.VISIBLE
        binding.searchListlbl.text = getString(R.string.recent_search)
        binding.edtSearch.visibility = View.INVISIBLE
        binding.mainSearchView.edtSearchMain.requestFocus()
        showSoftKeyboard(activity!!)


        searchViewModel.GetRecentSearchedUser(requireActivity(), session.token!!)
        searchViewModel.recentSearchedData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    if (response.data!!.status as Boolean) {
                        setRecentSearches(response.data.data)
                    }
                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                    requireContext().showToast("Error ${response.message}")
                }
            }

        })

        binding.mainSearchView.edtSearchMain.addTextChangedListener(searchListner)
    }

    private val searchListner = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.d("TextWatcher.. ", "$count")
            searchValue = s.toString()
            if (count == 0) {
                mainSearchAdapter.differ.submitList(searchedData)
            } else {
                jsonObject.addProperty("page", 1)
                jsonObject.addProperty("search", searchValue)
                searchViewModel.Search(activity!!, session.token!!, jsonObject)
            }
        }
    }


    private fun setRecentSearches(data: List<SearchList>) {
        searchedData = data
        mainSearchAdapter.differ.submitList(searchedData)
        binding.searchViewListRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.searchViewListRecycle.adapter = mainSearchAdapter
        mainSearchAdapter.notifyDataSetChanged()
    }
}