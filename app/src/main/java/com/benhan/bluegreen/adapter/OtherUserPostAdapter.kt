package com.benhan.bluegreen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.R
import com.benhan.bluegreen.dataclass.PostData
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class OtherUserPostAdapter(val context: Context, val postImageDataList: ArrayList<PostData>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    companion object{
        const val TYPE_POST = 0
        const val TYPE_LOAD = 1

    }

    var isLoading = false
    var isMoreDataAvailable = true
    var loadMoreListener = object :
        OnLoadMoreListener {
        override fun onLoadMore() {

        }




        }
    var onItemClickListener = object :
        OnItemClickListener {
        override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {

        }


    }

    class PostViewHolder(view: View): RecyclerView.ViewHolder(view){


        val ivPostImage = view.findViewById<ImageView>(R.id.postImage)

    }

    class LoadHolder(view: View): RecyclerView.ViewHolder(view){


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val itemLayout = LayoutInflater.from(context).inflate(R.layout.post_search_item, parent, false)
        val loadLayout = LayoutInflater.from(context).inflate(R.layout.search_recycler_load, parent, false)
        if(viewType == TYPE_POST)
        {return PostViewHolder(
            itemLayout
        )
        }
        else{
            return LoadHolder(
                loadLayout
            )
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if(position>= itemCount-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null)
        {
            isLoading = true
            loadMoreListener.onLoadMore()
        }


        val item = postImageDataList[position]


        if(getItemViewType(position) == TYPE_POST) {
            val postImage = item.postImage
            val postImageUri = MyApplication.severUrl + postImage

            Glide.with(context).load(postImageUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override((holder as PostViewHolder).ivPostImage.width, holder.ivPostImage.height)
                .centerCrop()
                .into(holder.ivPostImage)

            holder.ivPostImage.setOnClickListener {

                onItemClickListener.OnItemClick(holder, position)


            }


        }


    }

    override fun getItemViewType(position: Int): Int {

        if(postImageDataList[position].kind == "post"){
            return TYPE_POST
        }
        else{
            return TYPE_LOAD
        }

    }


    override fun getItemCount(): Int {
       return postImageDataList.size
    }


    fun notifyDataChanged(){
        notifyDataSetChanged()
        isLoading = false
    }

    fun addLoadMoreListener(listener: OnLoadMoreListener?) {
        loadMoreListener = listener!!
    }


    interface OnLoadMoreListener{
        fun onLoadMore()
    }
}