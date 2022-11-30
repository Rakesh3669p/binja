package com.app.fuse.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class ShrinkBehavior(context: Context, attributeSet: AttributeSet)
    : CoordinatorLayout.Behavior<View>(context, attributeSet){

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is BottomNavigationView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (parent == null || child == null || dependency == null)
            return false

        val distanceY = getViewOffsetForSnackbar(parent, child)
        val fractionComplete = distanceY / dependency.height
        val scaleFactor = 1 - fractionComplete

        child.scaleX = scaleFactor
        child.scaleY = scaleFactor
        return true
    }

    private fun getViewOffsetForSnackbar(parent: CoordinatorLayout, view: View): Float{
        var maxOffset = 0f
        val dependencies = parent.getDependencies(view)

        dependencies.forEach { dependency ->
            if (dependency is BottomNavigationView && parent.doViewsOverlap(view, dependency)){
                maxOffset =
                    maxOffset.coerceAtLeast((dependency.translationY - dependency.height) * -80)
            }
        }

        return maxOffset
    }
}