package com.app.fuse.ui.mainScreen.notification.gameRequests.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.fuse.R
import com.app.fuse.databinding.ItemGameRequestBinding
import com.app.fuse.ui.mainScreen.notification.gameRequests.model.GameRequestData
import com.app.fuse.utils.common.getPlaceHolderSearch
import com.bumptech.glide.Glide

class GameRequestAdapter : RecyclerView.Adapter< GameRequestAdapter .ViewHolder>() {
    lateinit var context: Context
    inner class ViewHolder(val binding: ItemGameRequestBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(model:GameRequestData){
            with(binding){
                if(model.from_users_data.profile==null){
                    Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch()).load(R.drawable.place_holder)
                }else{
                    Glide.with(context).setDefaultRequestOptions(getPlaceHolderSearch()).load(model.from_users_data.profile)
                }

                userName.text = model.from_users_data.username
                acceptCV.setOnClickListener {
                    onAcceptClickListner?.let { it(model) }
                }
            }
        }
    }


    private val diffCallBack = object : DiffUtil.ItemCallback<GameRequestData>() {
        override fun areItemsTheSame(oldItem: GameRequestData, newItem: GameRequestData): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(
            oldItem: GameRequestData,
            newItem: GameRequestData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    private var onAcceptClickListner: ((GameRequestData) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameRequestAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(ItemGameRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: GameRequestAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])

    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnAcceptClickListener(listener: (GameRequestData) -> Unit) {
        onAcceptClickListner = listener
    }
}