package com.app.fuse.ui.homescreen.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.FragmentMessagesBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.mainScreen.notification.messages.model.ConversationList
import com.app.fuse.ui.chatmodule.ChatActivity
import com.app.fuse.ui.chatmodule.ChatHistoryViewModel
import com.app.fuse.ui.chatmodule.ChatHistoryViewModelFactory
import com.app.fuse.ui.homescreen.messages.adapter.MessageAdapter
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.showToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject


class Messages : Fragment(R.layout.fragment_messages) {

    lateinit var session: SessionManager
    lateinit var binding: FragmentMessagesBinding
    var messagesAdapter: MessageAdapter = MessageAdapter()
    lateinit var navBottomView: BottomNavigationView
    lateinit var messageViewModel: MessageViewModel
    lateinit var chatViewModel: ChatHistoryViewModel
    var conversationList: MutableList<ConversationList> = ArrayList()
    val jsonObject = JsonObject()
    var pageNo = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMessagesBinding.bind(view)
        init()
        setRecycle()
        setViewModel()
    }


    private fun init() {
        session = SessionManager(requireContext())
        navBottomView =
            (this.context as AppCompatActivity).findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView

        messagesAdapter.setOnItemClickListener {

            startActivity(Intent(requireContext(), ChatActivity::class.java).apply {

                putExtra("friendID", it.id)
                putExtra("friendUserName", it.username)
                if(it.designation!=null){
                    putExtra("friendBio", it.designation)
                }else{
                    putExtra("friendBio", "")
                }

                putExtra("from", "ConversationScreen")

                if (it.profile == null) {
                    putExtra("profile", "")
                } else {
                    putExtra("profile", it.profile!!)
                }
            })
        }
    }

    private fun setViewModel() {
        val repository = BinjaRepository(BinjaDatabase(requireContext()))
        val messageViewModelFactory =
            MessageViewModelFactory(requireActivity().application, repository)
        messageViewModel =
            ViewModelProvider(this, messageViewModelFactory).get(MessageViewModel::class.java)

        val chatViewModelFactory =
            ChatHistoryViewModelFactory(requireActivity().application, repository)
        chatViewModel =
            ViewModelProvider(this, chatViewModelFactory).get(ChatHistoryViewModel::class.java)

        jsonObject.addProperty("page", 1)
        messageViewModel.Conversation(requireActivity(), jsonObject)
        setObserver()
    }

    private fun setObserver() {
        messageViewModel.conversation.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resources.Success -> {
                    if (response.data!!.status) {
                        binding.progressBar.pgrBarLayout.visibility = View.GONE
                        setConversation(response.data.data)
                    }
                }
                is Resources.Loading -> {
                    binding.progressBar.pgrBarLayout.visibility = View.VISIBLE
                }
                is Resources.Error -> {
                    binding.progressBar.pgrBarLayout.visibility = View.GONE
                    requireContext().showToast(response.message.toString())
                }
            }

        })
    }

    private fun setConversation(conversationData: List<ConversationList>) {
        conversationData.forEach { it ->
            if (!conversationList.contains(it)) {
                conversationList.addAll(listOf(it))
            }
        }

        messagesAdapter.differ.submitList(conversationList)
        messagesAdapter.notifyDataSetChanged()

    }

    private fun setRecycle() {
        binding.chatListRecycle.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messagesAdapter
            messagesAdapter.notifyDataSetChanged()
            addOnScrollListener(scrollListner)
        }
    }


    var isLoading = false
    var isScrolling = false
    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            Log.d("Dragging..", "$dy $dx")
            if (dy > 0) {
                val recycleLayoutManager =
                    binding.chatListRecycle.layoutManager as LinearLayoutManager
                if (!isLoading) {

                    if (recycleLayoutManager != null && recycleLayoutManager.findLastCompletelyVisibleItemPosition() == messagesAdapter.itemCount - 1) {
                        pageNo++
                        jsonObject.addProperty("page", pageNo)
                        messageViewModel.Conversation(
                            requireActivity(),
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