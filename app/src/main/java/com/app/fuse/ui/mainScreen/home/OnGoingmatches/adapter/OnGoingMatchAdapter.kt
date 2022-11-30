package com.app.fuse.ui.mainScreen.home.OnGoingmatches.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.ItemOngoingMatchBinding
import com.app.fuse.ui.mainScreen.home.OnGoingmatches.model.OnGoingMatchList
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide

class OnGoingMatchAdapter : RecyclerView.Adapter<OnGoingMatchAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var session: SessionManager

    inner class ViewHolder(val binding: ItemOngoingMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OnGoingMatchList) {
            with(binding) {
                Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch())
                    .load(data.profile).into(userImage)
                friendListUserName.text = data.username

                if (data.match_games_result.to_user_id == session.userID) {
                    matchInProgrssText.text = context.getString(R.string.matching_with_you)
                } else {
                    matchInProgrssText.text = context.getString(R.string.match_in_progress)
                }
                friendListStartMatchCV.setOnClickListener {
                    if (matchInProgrssText.text  == context.getString(R.string.matching_with_you)) {
                        onMatchWithYouListner?.let { it(data) }
                    } else {
                        onMatchStartListner?.let { it(data) }
                    }
                }
            }
        }
    }


    private val diffCallBack = object : DiffUtil.ItemCallback<OnGoingMatchList>() {
        override fun areItemsTheSame(
            oldItem: OnGoingMatchList,
            newItem: OnGoingMatchList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: OnGoingMatchList,
            newItem: OnGoingMatchList
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnGoingMatchAdapter.ViewHolder {
        context = parent.context
        session = SessionManager(context)
        return ViewHolder(
            ItemOngoingMatchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    private var onMatchStartListner: ((OnGoingMatchList) -> Unit)? = null
    private var onMatchWithYouListner: ((OnGoingMatchList) -> Unit)? = null


    override fun onBindViewHolder(holder: OnGoingMatchAdapter.ViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.bind(model)

    }

    fun setOnStartMatchListner(listener: (OnGoingMatchList) -> Unit) {
        onMatchStartListner = listener
    }

    fun setOnMatchWithYouListner(listener: (OnGoingMatchList) -> Unit) {
        onMatchWithYouListner = listener
    }

    override fun getItemCount(): Int = differ.currentList.size
}