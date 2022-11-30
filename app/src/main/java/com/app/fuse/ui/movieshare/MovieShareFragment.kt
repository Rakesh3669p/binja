package com.app.fuse.ui.movieshare

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.FragmentMovieShareBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.homescreen.search.SearchViewModel
import com.app.fuse.ui.homescreen.search.SearchViewModelFactory
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.hideKeyboard
import com.app.fuse.utils.common.showToast
import com.app.fuse.ui.mainScreen.search.model.SearchList
import com.app.fuse.ui.movieshare.adapter.UserSearchAdapter
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.show
import com.google.gson.JsonObject


class MovieShareFragment : Fragment(R.layout.fragment_movie_share), View.OnClickListener{
    lateinit var binding: FragmentMovieShareBinding
    lateinit var session: SessionManager

    var userSearchAdapter: UserSearchAdapter = UserSearchAdapter()
    var searchDataList: MutableList<SearchList> = ArrayList()
    var searchedData: List<SearchList> = ArrayList()

    lateinit var searchViewModel: SearchViewModel
    var pageNo = 1
    var searchValue = ""
    val jsonObject = JsonObject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieShareBinding.bind(view)
        init()
        setOnClickListners()
        searchViewModel()
        setRecycle()
    }

    private fun init() {
        session = SessionManager(requireContext())


    }

    private fun searchViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val searchViewModelFactory = SearchViewModelFactory(activity!!.application, binjaRepository)
        searchViewModel = ViewModelProvider(this, searchViewModelFactory).get(SearchViewModel::class.java)
        searchViewModel.GetRecentSearchedUser(activity!!, session.token!!)
        setObserver()
    }

    private fun setObserver() {
        searchViewModel.recentSearchedData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Success -> {
                    binding.progressBar.pgrBarLayout.hide(requireActivity())

                    response.data.let { searchResponse ->
                        if (searchResponse!!.data.isNotEmpty()) {
                            setSearchData(response.data!!.data, "recent")
                            isLoading = false
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
        }
        searchViewModel.searchData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Success -> {

                    binding.progressBar.pgrBarLayout.hide(requireActivity())

                    Log.d("SearhData...", response.data.toString())

                    response.data.let { searchResponse ->
                        if (searchResponse!!.data.rows.isNotEmpty()) {
                            setSearchData(response.data!!.data.rows, "search")
                            isLoading = false
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
        }

    }




    private fun setSearchData(searchData: List<SearchList>,type:String) {
        if(type=="search"){
            searchDataList.clear()
        }
              searchData.forEach { it ->
            if (searchDataList.contains(it)) {
            } else {
                searchDataList.addAll(listOf(it))
            }
        }
        userSearchAdapter.differ.submitList(searchDataList)
        userSearchAdapter.notifyDataSetChanged()

    }

    private fun setOnClickListners() {
        binding.backArrow.setOnClickListener(this)
        binding.edtSearchFriends.addTextChangedListener(textWatcher)
    }


    private fun setRecycle() {
        binding.friendsSearchRecycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userSearchAdapter
            userSearchAdapter.notifyDataSetChanged()
            addOnScrollListener(this@MovieShareFragment.scrollListner)

        }
    }


    var isLoading = false
    var isScrolling = false


    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (dy > 0) {

                val recycleLayoutManager = binding.friendsSearchRecycle.layoutManager as LinearLayoutManager
                if (!isLoading) {

                    if (recycleLayoutManager != null && recycleLayoutManager.findLastCompletelyVisibleItemPosition() == userSearchAdapter.itemCount - 1) {
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

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backArrow -> {
                hideKeyboard(activity!!)
                findNavController().navigateUp()
            }
        }
    }




    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            Log.d("TextChanged.. ", "$s")

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            Log.d("TextChangedBed.. ", "$s")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.d("TextWatcher.. ", "$count")
            searchValue = s.toString()
            if (count == 0) {
                userSearchAdapter.differ.submitList(searchedData)
            } else {
                jsonObject.addProperty("page", 1)
                jsonObject.addProperty("search", searchValue)
                searchViewModel.Search(activity!!, session.token!!, jsonObject)
            }
        }
    }
}