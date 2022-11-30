package com.app.fuse.ui.chatmodule

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.ActivityChat2Binding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.login.model.RegisterData
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.*
import com.bumptech.glide.Glide
import com.app.fuse.ui.chatmodule.adapter.ChatAdapter
import com.app.fuse.ui.chatmodule.model.ChatData
import com.app.fuse.ui.chatmodule.model.ChatDataList
import com.app.fuse.utils.Constants
import com.app.fuse.utils.SocketManager
import com.github.nkzawa.utf8.UTF8
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


class ChatActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityChat2Binding
    private var mSocket: Socket? = null
    lateinit var session: SessionManager
    private var chatAdapter: ChatAdapter = ChatAdapter()
    lateinit var chatHistoryViewModel: ChatHistoryViewModel
    var chatHistoryList: MutableList<ChatDataList> = ArrayList()
    var  from=""
    val jsonObject: JsonObject = JsonObject()
    private var verticalScrollOffset = AtomicInteger(0)
    var userID = 0
    var friendUserID = 0
    var friendUserName = ""
    var pageNo = 1
    var count = 0
    var receiverProfile = ""
    var receiverBio = ""
    var senderProfile = ""
    var isLoading = false
    var isScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChat2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        SocketConnection()
        setRecycle()
        setViewModel()
        setOnClickListners()
        setChatCount()
    }

    private fun setChatCount() {
        chatHistoryViewModel.ChatCount(this)

        chatHistoryViewModel.chatCount.observe(this, { response ->
            when (response) {
                is Resources.Success -> {
                    session.messageCount = response.data!!.data.unReadTotal
                }
                is Resources.Loading -> {
                }
                is Resources.Error -> {

                }
            }
        })
    }


    private fun init() {
        session = SessionManager(applicationContext)
        val gson = Gson()
        val loginData: RegisterData = gson.fromJson(session.userLoginData, RegisterData::class.java)
        val extras = intent.extras
        senderProfile = loginData.profile
        userID = loginData.id
        friendUserID = extras!!.getInt("friendID")
        friendUserName = extras.getString("friendUserName")!!
        receiverProfile = extras.getString("profile")!!
        receiverBio = extras.getString("friendBio")!!
        from = extras.getString("from")!!
        binding.toolbarTitle.text = friendUserName

    }

    private fun SocketConnection() {
        SocketManager.instance!!.connectSocket(session.token!!, Constants.BASE_URL)
        mSocket = SocketManager.instance!!.getSocket()
        mSocket!!.connect()

        mSocket!!.on("new_message", onNewMessage)
    }


    private fun setRecycle() {
        binding.rvChatHistory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            isNestedScrollingEnabled = true
            adapter = chatAdapter
            scrollToPosition(0)
            addOnScrollListener(this@ChatActivity.scrollListner)

        }
            .addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                val y = oldBottom - bottom
                if (y.absoluteValue > 0) {
                    binding.rvChatHistory.post {
                        if (y > 0 || verticalScrollOffset.get().absoluteValue >= y.absoluteValue) {
                            binding.rvChatHistory.scrollBy(0, y)
                        } else {
                            binding.rvChatHistory.scrollBy(0, verticalScrollOffset.get())
                        }
                    }
                }
            }
    }


    private fun setViewModel() {
        val repository = BinjaRepository(BinjaDatabase(this))
        val chatHistoryViewModelFactory = ChatHistoryViewModelFactory(application, repository)
        chatHistoryViewModel = ViewModelProvider(
            this,
            chatHistoryViewModelFactory
        ).get(ChatHistoryViewModel::class.java)
        jsonObject.addProperty("chat_user_id", friendUserID)
        jsonObject.addProperty("page", pageNo)
        chatHistoryViewModel.ChatHistory(this, session.token!!, jsonObject)
        setObserver()
    }

    private fun setObserver() {
        chatHistoryViewModel.chatHistoryData.observe(this, androidx.lifecycle.Observer { response ->
            when (response) {
                is Resources.Success -> {
                    binding.loading.pgrBarLayout.hide(this)
                    if (response.data!!.status) {
                        setChatHistory(response.data.data)
                    }
                }
                is Resources.Loading -> {
                    binding.loading.pgrBarLayout.show(this)
                }
                is Resources.Error -> {
                    binding.loading.pgrBarLayout.hide(this)
                }
            }
        })
    }

    private fun setChatHistory(chatHistorydata: ChatData) {
        count = chatHistorydata.count
        chatAdapter.addMessageCount(count)
        isLoading = chatHistorydata.count == chatHistoryList.size

        if (count == 0) {
            binding.noChatLayout.profileView.visibility = View.VISIBLE
            Glide.with(this).setDefaultRequestOptions(getPlaceHolderSearch()).load(receiverProfile)
                .into(
                    binding.noChatLayout.singleUserViewImage
                )
            binding.noChatLayout.singleViewUserDesc.text = receiverBio

        } else {
            binding.noChatLayout.profileView.visibility = View.GONE
        }

        if (chatHistorydata.count != 0) {
            chatHistoryList.addAll(chatHistorydata.rows)
            chatAdapter.differ.submitList(chatHistoryList)
            chatAdapter.notifyDataSetChanged()

            when {
                chatHistoryList.size <= 25 -> {
                    binding.rvChatHistory.scrollToPosition(0)
                }
                chatHistoryList.size == count -> {
                    binding.rvChatHistory.scrollToPosition(chatHistoryList.size)
                }
                else -> {
                    binding.rvChatHistory.scrollToPosition(chatHistoryList.size - 25)
                }
            }
        }
    }


    private val onNewMessage: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                Log.d("TAG", "Chatdata......$data")
                val id: Int
                val from_user_id: Int
                val to_user_id: Int
                val message: String
                val created_at: String
                val time: String
                val message_postion: String


                try {

                    id = data.getInt("id")
                    from_user_id = data.getInt("from_user_id")
                    to_user_id = data.getInt("to_user_id")
                    message = data.getString("message")
                    created_at = data.getString("created_at")
                    time = data.getString("time")
                    message_postion = data.getString("message_postion")

                } catch (e: JSONException) {
                    return@Runnable
                }

                addMessage(
                    message,
                    from_user_id,
                    to_user_id,
                    message_postion,
                    receiverProfile
                )

            })
        }
    }

    private fun sendNewMessage() {
        if (mSocket!!.connected()) {
            SendMessageThroughSocket()

        } else {
            SendMessageThroughApi()

        }

    }

    private fun SendMessageThroughApi() {
        val message = UTF8.encode(binding.edtTextMessage.text.toString().trim())
        if (message.isNotBlank() && message.isNotEmpty()) {
            binding.edtTextMessage.setText("")
            val createMessageData = JsonObject()
            createMessageData.addProperty("to_user_id",friendUserID)
            createMessageData.addProperty("msg",message)
            chatHistoryViewModel.CreateMessage(this,session.token!!,createMessageData)
            addMessage(message, userID, friendUserID, "right", senderProfile)
        } else {
            showToast("Message Cannot be Blank")
        }

    }

    private fun SendMessageThroughSocket() {
        val message = UTF8.encode(binding.edtTextMessage.text.toString().trim())

        if (message.isNotBlank() && message.isNotEmpty()) {
            binding.edtTextMessage.setText("")

            val addMessageObject = JSONObject()
                try {
                addMessageObject.put("from_user_id", userID)
                addMessageObject.put("to_user_id", friendUserID)
                addMessageObject.put("message", message)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            mSocket!!.emit("send_message", addMessageObject)
            addMessage(message, userID, friendUserID, "right", senderProfile)
        } else {
            showToast("Message Cannot be Blank")
        }
    }

    private fun addMessage(
        message: String,
        userID: Int,
        friendUserID: Int,
        msgFrom: String,
        profile: String
    ) {

        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val currentDate = dateFormat.format(Date())

        val date: Date = dateFormat.parse(currentDate)
        val time: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
        count++

        val chatData = ChatDataList(
            currentDate,
            userID,
            null,
            message,
            time,
            msgFrom,
            profile,
            friendUserID
        )

        chatHistoryList.add(0, chatData)

        if (chatHistoryList.size > 0) {
            binding.noChatLayout.profileView.visibility = View.GONE

        } else {
            binding.noChatLayout.profileView.visibility = View.VISIBLE

        }

        chatAdapter.differ.submitList(chatHistoryList)
        chatAdapter.notifyItemInserted(0)
        chatAdapter.addMessageCount(count)
        scrollToBottom()


    }


    private fun scrollToBottom() {
        binding.rvChatHistory.scrollToPosition(0)
    }

    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            Log.d("TAG", "Scrolled $dx   $dy")
            if (dy < 0) {
                val recycleLayoutManager =
                    binding.rvChatHistory.layoutManager as LinearLayoutManager

                if (!isLoading) {
                    if (chatHistoryList.size != count) {
                        if (recycleLayoutManager.findLastCompletelyVisibleItemPosition() == chatAdapter.itemCount - 1) {
                            binding.loading.pgrBarLayout.show(this@ChatActivity)
                            pageNo++
                            jsonObject.addProperty("chat_user_id", friendUserID)
                            jsonObject.addProperty("page", pageNo)
                            chatHistoryViewModel.ChatHistory(
                                this@ChatActivity,
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

    private fun setOnClickListners() {
        binding.ivSend.setOnClickListener(this)
        binding.backArrow.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backArrow -> {
                hideKeyboard(this@ChatActivity)
                onBackPressed()
            }
            R.id.iv_send -> {
                sendNewMessage()
            }
        }
    }

    override fun onBackPressed() {
        session.isFromChatScreen = from != "ConversationScreen"
        super.onBackPressed()
    }
}

