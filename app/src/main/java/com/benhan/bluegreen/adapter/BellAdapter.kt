package com.benhan.bluegreen.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.*
import com.benhan.bluegreen.dataclass.BellData
import com.benhan.bluegreen.fullpost.FullPost
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.user.OtherUser
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BellAdapter(val context: Context, val bellDataList: ArrayList<BellData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val TYPE_BELL = 0
        const val TYPE_LOAD = 1

    }

    var isLoading = false
    var isMoreDataAvailable = true
    var onLoadMoreListener: HomeRecyclerAdapter.OnLoadMoreListener? = null
    val sharedPreference = SharedPreference()

    class BellHolder(view: View): RecyclerView.ViewHolder(view){

        val ivProfile: ImageView = view.findViewById(R.id.profilePhoto)
        val tvName: TextView = view.findViewById(R.id.posterName)
        val tvDescription: TextView = view.findViewById(R.id.descriptionContents)
        val tvDate: TextView = view.findViewById(R.id.postedDate)
        val ivPostImage: ImageView = view.findViewById(R.id.postImage)
    }

    class LoadHolder(view: View): RecyclerView.ViewHolder(view){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.bell_recycler_item, parent, false)
        val loadLayout = LayoutInflater.from(context).inflate(R.layout.search_recycler_load, parent, false)
        if(viewType == TYPE_BELL) {
            return BellHolder(layout)
        } else {
            return LoadHolder(loadLayout)
        }
    }

    override fun getItemViewType(position: Int): Int {

        if(bellDataList[position].kind == "bell"){
            return SearchRecyclerAdapter.TYPE_PLACE
        }else{
            return SearchRecyclerAdapter.TYPE_LOAD
        }
    }

    override fun getItemCount(): Int {
        return bellDataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(position >= itemCount-1 && isMoreDataAvailable && !isLoading && onLoadMoreListener!=null)

        {
            isLoading = true
            onLoadMoreListener!!.onLoadMore()
        }

        if(getItemViewType(position) == TYPE_BELL) {
            val item = bellDataList[position]
            val myName = sharedPreference.getString(context, "name")

            val prettyTime = PrettyTime(Locale.KOREA)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val date = simpleDateFormat.parse(item.date)
            val ago = prettyTime.format(date)

            (holder as BellHolder).tvName.text = item.user_name
            holder.tvDate.text = ago

            val profilePhotoUri = MyApplication.severUrl+item.profile_photo
            val postImageUri = MyApplication.severUrl + item.post_image
            Glide.with(context).load(profilePhotoUri)
                .override(holder.ivProfile.width, holder.ivProfile.height)
                .into(holder.ivProfile)
            Glide.with(context).load(postImageUri)
                .override(holder.ivPostImage.width, holder.ivPostImage.height)
                .into(holder.ivPostImage)

            if (item.type == "like") {
                holder.tvDescription.text = "님이 회원님의 사진을 좋아합니다."
            } else {
                holder.tvDescription.text = "님이 회원님의 사진에 댓글을 남겼습니다."
            }


            holder.ivProfile.setOnClickListener {
                startOtherUserPage(item.user_name!!)
            }
            holder.tvName.setOnClickListener {
                startOtherUserPage(item.user_name!!)
            }

            holder.ivPostImage.setOnClickListener {
                goToPost(myName!!, item.post_id!!)
            }

        }


    }

    fun startOtherUserPage(userName: String) {
        val intent = Intent(context, OtherUser::class.java)
        intent.putExtra("otherUserName", userName)
        context.startActivity(intent)
    }


    fun goToPost(userName: String, postId: Int){
        val intent = Intent(context, FullPost::class.java)
        intent.putExtra("place_or_user_name", userName)
        intent.putExtra("post_id", postId)
        context.startActivity(intent)
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
        isLoading = false
    }



}