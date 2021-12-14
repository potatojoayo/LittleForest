package com.benhan.bluegreen.utill

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridDividerDecoration(resources: Resources, layout: Int): RecyclerView.ItemDecoration() {

    companion object {

        val ATTRS = intArrayOf(android.R.attr.listDivider)

    }

    val mDivider = resources.getDrawable(layout, null)
    val mInsets = 1


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }


    fun drawVertical(c: Canvas, parent: RecyclerView) {
        if (parent.childCount == 0) return

        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val child = parent.getChildAt(0)
        if (child.height == 0) return

        val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
        var top = child.bottom + params.bottomMargin + mInsets
        var bottom = top + mDivider.intrinsicHeight

        val parentBottom = parent.height - parent.paddingBottom
        while (bottom < parentBottom) {
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)

            top += mInsets + params.topMargin + child.height + params.bottomMargin + mInsets
            bottom = top + mDivider.intrinsicHeight

        }

    }

    fun drawHorizontal(c: Canvas, parent: RecyclerView) {

        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0..childCount) {

            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin + mInsets
            val right = left + mDivider.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)

        }
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(mInsets, mInsets, mInsets, mInsets)
    }

}




