package com.app.fuse.ui.mainScreen.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.fuse.R
import com.app.fuse.databinding.FragmentNotificationBinding
import com.app.fuse.ui.homescreen.messages.Messages
import com.app.fuse.ui.mainScreen.notification.gameRequests.RequestGameFragment
import com.app.fuse.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_home_main.*


class NotificationFragment : Fragment(R.layout.fragment_notification) {

    lateinit var binding: FragmentNotificationBinding
    lateinit var session:SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationBinding.bind(view)
        init()
        setTabView()
    }

    private fun init() {
        session = SessionManager(requireContext())
    }

    private fun setTabView() {
        val viewPager = binding.viewpager
        setupViewPager(viewPager)
        val tabs = binding.resultTabs
        tabs.setupWithViewPager(viewPager)

    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(childFragmentManager)
        adapter.addFragment(Messages(), "Messages")
        adapter.addFragment(RequestGameFragment(), "Game Requests")
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
}