package com.app.fuse.ui.moviesearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fuse.R
import com.app.fuse.databinding.FragmentMovieSearchBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.hideKeyboard
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.app.fuse.ui.moviesearch.adapter.MovieSearchAdapter
import com.app.fuse.ui.moviesearch.adapter.TopMovieMatchesAdapter
import com.app.fuse.ui.moviesearch.model.MovieSearchData
import com.app.fuse.ui.moviesearch.model.MovieSearchedList
import com.app.fuse.ui.moviesearch.model.TopMovieMatchData
import com.google.gson.JsonObject


class MovieSearch : Fragment(R.layout.fragment_movie_search), View.OnClickListener,
    View.OnTouchListener {
    lateinit var binding: FragmentMovieSearchBinding
    lateinit var session:SessionManager
    lateinit var movieSearchViewModel: MovieSearchViewModel

    private var movieSearchAdapter: MovieSearchAdapter = MovieSearchAdapter()
    private var topMovieMatchesAdapter: TopMovieMatchesAdapter = TopMovieMatchesAdapter()
    private var topMoviesData:List<TopMovieMatchData> = ArrayList()

    var searchValue = ""
    var moviesSearchedData:List<MovieSearchedList> = ArrayList()
    var isInSearchMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieSearchBinding.bind(view)
        init()
        setOnBackPress()
        setOnClickListners()
        setViewModel()
        setRecycle()
    }

    private fun setOnClickListners() {
        binding.backArrow.setOnClickListener(this)
    }

    private fun setRecycle() {
        binding.movieSearchRecycle.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = movieSearchAdapter
            movieSearchAdapter.notifyDataSetChanged()
        }
    }

    private fun setViewModel() {
        val repository = BinjaRepository(BinjaDatabase(requireContext()))
        val movieSearchViewModelFactory = MovieSearchViewModelFactory(requireActivity().application,repository)
        movieSearchViewModel = ViewModelProvider(this,movieSearchViewModelFactory).get(MovieSearchViewModel::class.java)
        movieSearchViewModel.TopMatcheMovies(requireActivity(),session.token!!)
        setObserver()
 }

    private fun setObserver() {
        movieSearchViewModel.topMatchMovie.observe(viewLifecycleOwner, Observer {response->
            when(response){

                is Resources.Success->{
                    binding.progressBar.pgrBarLayout.hide(requireActivity())

                    if(response.data!!.status){
                        setTopMovieMatches(response.data.data)
                    }else{
                        requireContext().showToast(response.data.msg)
                    }
                }
                is Resources.Loading->{
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                }
                is Resources.Error->{
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                }
            }

        })

        movieSearchViewModel.searchMovie.observe(viewLifecycleOwner, Observer {response->
            when(response){

                is Resources.Success->{
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                        if(response.data!!.status){
                            setMovieSearch(response.data.data)
                        }else{
                            requireContext().showToast(response.data.msg)

                    }

                }
                is Resources.Loading->{
                    binding.progressBar.pgrBarLayout.show(requireActivity())
                }
                is Resources.Error->{
                    binding.progressBar.pgrBarLayout.hide(requireActivity())
                }
            }

        })
    }

    private fun setMovieSearch(searchMovieData: MovieSearchData) {
        binding.searchResultlbl.visibility = View.GONE
        binding.topMatcheslbl.visibility = View.GONE
        if (searchMovieData.rows.isEmpty()){
            binding.searchResultlbl.visibility = View.VISIBLE
            binding.searchResultlbl.text = getString(R.string.no_matches_found)
        }
        movieSearchAdapter.differ.submitList(searchMovieData.rows)
        binding.movieSearchRecycle.layoutManager =  LinearLayoutManager(requireContext())
        binding.movieSearchRecycle.adapter = movieSearchAdapter
    }

    private fun setTopMovieMatches(topMovieMatchData: List<TopMovieMatchData>) {
        binding.topMatcheslbl.visibility = View.VISIBLE
        binding.searchResultlbl.visibility = View.GONE
        topMoviesData = topMovieMatchData
        topMovieMatchesAdapter.differ.submitList(topMovieMatchData)
        binding.movieSearchRecycle.layoutManager =  GridLayoutManager(requireContext(),2)
        binding.movieSearchRecycle.adapter = topMovieMatchesAdapter
    }

    private fun init() {
        session = SessionManager(requireContext())
        binding.edtSearchMovie.addTextChangedListener(movieSeachWatcher)
        binding.edtSearchMovie.setOnTouchListener(this)
        val bundle =  Bundle()
        movieSearchAdapter.setOnShareClickListener {
            hideKeyboard(requireActivity())
            binding.edtSearchMovie.clearFocus()
            binding.edtSearchMovie.text.clear()
            bundle.putInt("movieID",it.id)
            findNavController().navigate(R.id.action_fragmentMovieSearch_to_movieShareFragment,bundle)
        }
        
        movieSearchAdapter.setOnItemClickListener {

            hideKeyboard(requireActivity())
            binding.edtSearchMovie.clearFocus()
            binding.edtSearchMovie.text.clear()
            bundle.putInt("movieID",it)
            findNavController().navigate(R.id.friendsMovieMatched,bundle)
        }

        topMovieMatchesAdapter.setOnShareClickListener {
            hideKeyboard(requireActivity())
            binding.edtSearchMovie.clearFocus()
            binding.edtSearchMovie.text.clear()
            bundle.putInt("movieID",it.id)
            findNavController().navigate(R.id.action_fragmentMovieSearch_to_movieShareFragment,bundle)
        }

        topMovieMatchesAdapter.setOnItemClickListner {
            hideKeyboard(requireActivity())
            binding.edtSearchMovie.clearFocus()
            binding.edtSearchMovie.text.clear()

            bundle.putInt("movieID",it)
            findNavController().navigate(R.id.friendsMovieMatched,bundle)
        }

    }

    private fun setOnBackPress() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if(isInSearchMode){
                        isInSearchMode = false
                        setTopMovieMatches(topMoviesData)
                        hideKeyboard(requireActivity())
                        binding.edtSearchMovie.clearFocus()
                    }else{
                        val graph = findNavController().navInflater.inflate(R.navigation.navigation_graph)
                        graph.startDestination = R.id.homeMainFragment
                        findNavController().graph = graph
                    }

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    private val movieSeachWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchValue = s.toString()
            if (count == 0) {

                movieSearchAdapter.differ.submitList(emptyList())
                binding.searchResultlbl.visibility = View.VISIBLE
                binding.searchResultlbl.text = getString(R.string.search_result_lbl)
            } else {
                val jsonObject  = JsonObject()
                jsonObject.addProperty("page", 1)
                jsonObject.addProperty("search", searchValue)
                movieSearchViewModel.SearchMovies(activity!!, session.token!!, jsonObject)

            }

        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.backArrow->{
                onBackpresses()
            }
        }
    }

    private fun onBackpresses() {
        if(isInSearchMode){
            isInSearchMode = false
            setTopMovieMatches(topMoviesData)
            hideKeyboard(requireActivity())
            binding.edtSearchMovie.clearFocus()
        }else{
            val graph = findNavController().navInflater.inflate(R.navigation.navigation_graph)
            graph.startDestination = R.id.homeMainFragment
            findNavController().graph = graph
        }

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
       when(v!!.id){
           R.id.edtSearchMovie->{
               isInSearchMode = true
               binding.searchResultlbl.visibility = View.VISIBLE
               binding.topMatcheslbl.visibility = View.GONE
               binding.edtSearchMovie.requestFocus()
               binding.movieSearchRecycle.layoutManager =  LinearLayoutManager(requireContext())
               binding.movieSearchRecycle.adapter = movieSearchAdapter
           }
       }
        return false
    }
}


