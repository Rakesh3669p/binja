package com.app.fuse.ui.mainScreen.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.app.fuse.R
import com.app.fuse.databinding.FragmentHomeMainBinding
import com.app.fuse.ui.mainScreen.home.FriendsList.Home
import com.app.fuse.ui.mainScreen.home.OnGoingmatches.OnGoingmatchesFragmnet
import com.app.fuse.utils.common.hideKeyboard


class HomeMainFragment : Fragment(R.layout.fragment_home_main), View.OnClickListener {

    lateinit var binding: FragmentHomeMainBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeMainBinding.bind(view)
        setTabView()
        setOnClickListner()
    }

    private fun setOnClickListner() {
        binding.movieSearch.setOnClickListener(this)
    }

    private fun setTabView() {
        val viewPager = binding.viewpager
        setupViewPager(viewPager)
        val tabs = binding.homeTabs
        tabs.setupWithViewPager(viewPager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(childFragmentManager)
        adapter.addFragment(Home(), "Freinds")
        adapter.addFragment(OnGoingmatchesFragmnet(), "OnGoing Matches")
        viewPager.adapter = adapter
    }

    internal class Adapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.movieSearch->{
                hideKeyboard(requireActivity())
                findNavController().navigate(R.id.fragmentMovieSearch)
            }
        }
    }
}