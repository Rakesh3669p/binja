package com.app.fuse.ui.homescreen.home.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendsConnectionList
import com.app.fuse.ui.chatmodule.ChatActivity
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.getPlaceHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_friends.view.*

class FriendListAdapter : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {
    lateinit var session: SessionManager

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)


    private val differCallBack = object : DiffUtil.ItemCallback<FriendsConnectionList>() {
        override fun areItemsTheSame(
            oldItem: FriendsConnectionList,
            newItem: FriendsConnectionList
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FriendsConnectionList,
            newItem: FriendsConnectionList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        session = SessionManager(parent.context)
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_friends, parent, false)
        )
    }

    private var onStartMatchClickListner: ((FriendsConnectionList) -> Unit)? = null


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = differ.currentList[position]

        holder.itemView.apply {
            val     bundle = Bundle()
            Glide.with(context).setDefaultRequestOptions(getPlaceHolder())
                .load(model.friend_user!!.profile).into(userImage)
            friendListUserName.text = model.friend_user.username

            /* if (model.friend_user.match_in_progress == 1 && model.friend_user.match_with == session.userID) {
                 friendListStartMatchCV.isClickable = false
                 startMatchtxt.text = "Matching With You"

                 startMatchtxt.setTextColor(ContextCompat.getColor(context, R.color.white))
                 friendListStartMatchCV.setCardBackgroundColor(
                     ContextCompat.getColor(
                         context,
                         R.color.white_700
                     )
                 )
             } else if (model.friend_user.match_in_progress == 1) {
                 friendListStartMatchCV.isClickable = false
                 startMatchtxt.text = "Match In Progress"
                 startMatchtxt.setTextColor(ContextCompat.getColor(context, R.color.white))
                 friendListStartMatchCV.setCardBackgroundColor(
                     ContextCompat.getColor(
                         context,
                         R.color.white_700
                     )
                 )
             }else{
                 friendListStartMatchCV.isClickable = true
                 startMatchtxt.text = "Start Match"
                 startMatchtxt.setTextColor(ContextCompat.getColor(context, R.color.red))
                 friendListStartMatchCV.setCardBackgroundColor(
                     ContextCompat.getColor(
                         context,
                         R.color.white
                     )
                 )
             }*/

            setOnClickListener {
                bundle.putInt("friendID", model?.friend_user.id!!)
                bundle.putString("from", "start")
                findNavController().navigate(R.id.fragmnetMovieMatchedList, bundle)
            }

            friendListStartMatchCV.setOnClickListener {
                bundle.putInt("friendID", model.friend_user?.id!!)
                bundle.putString("from", "Home")
                bundle.putInt("onGoingMatch", model.friend_user.on_goin_match!!)
                session.channelfilters = emptySet()
                session.genrefilters = emptySet()
                onStartMatchClickListner?.let { it(model) }
                findNavController().navigate(R.id.filterFragment, bundle)
            }
            friendListMessageCV.setOnClickListener {
                context.startActivity(Intent(context, ChatActivity::class.java).apply {
                    putExtra("friendID", model.friend_user.id)
                    putExtra("friendUserName", model.friend_user.username)
                    putExtra("from", "HomeScreen")

                    if (model.friend_user.profile != null) {
                        putExtra("profile", model.friend_user.profile)
                    } else {
                        putExtra("profile", "")
                    }
                    if (model.friend_user.designation != null) {
                        putExtra("friendBio", model.friend_user.designation)
                    } else {
                        putExtra("friendBio", "")
                    }

                })
            }
        }

    }

    fun setOnStartMatchClickListner(listener: (FriendsConnectionList) -> Unit) {
        onStartMatchClickListner = listener
    }

    override fun getItemCount(): Int = differ.currentList.size
}