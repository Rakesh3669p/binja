package com.app.fuse.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView


class NestedScrollingView : NestedScrollView {
    private var mState = RecyclerView.SCROLL_STATE_IDLE

    interface NestedScrollViewScrollStateListener {
        fun onNestedScrollViewStateChanged(state: Int)
    }

    fun setScrollListener(scrollListener: NestedScrollViewScrollStateListener?) {
        mScrollListener = scrollListener
    }

    private var mScrollListener: NestedScrollViewScrollStateListener? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
    }

    override fun stopNestedScroll() {
        super.stopNestedScroll()
        dispatchScrollState(RecyclerView.SCROLL_STATE_IDLE)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        dispatchScrollState(RecyclerView.SCROLL_STATE_DRAGGING)
        return super.onStartNestedScroll(child!!, target!!, nestedScrollAxes)
    }

    override fun startNestedScroll(axes: Int): Boolean {
        val superScroll = super.startNestedScroll(axes)
        dispatchScrollState(RecyclerView.SCROLL_STATE_DRAGGING)
        return superScroll
    }

    private fun dispatchScrollState(state: Int) {
        if (mScrollListener != null && mState != state) {
            mScrollListener!!.onNestedScrollViewStateChanged(state)
            mState = state
        }
    }
}