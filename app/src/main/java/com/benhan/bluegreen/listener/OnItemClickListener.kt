package com.benhan.bluegreen.listener

import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {

    fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int)
}