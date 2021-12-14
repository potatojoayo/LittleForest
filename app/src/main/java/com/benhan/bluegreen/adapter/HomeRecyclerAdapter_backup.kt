package com.benhan.bluegreen.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.R
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.dataclass.PostData
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class HomeRecyclerAdapter_backup(val context: Context, val activity: Activity, var postList: ArrayList<PostData>, val profilePhoto: String ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        const val TYPE_POST = 0
        const val TYPE_LOAD = 1

    }

    private var isLoading = false
    var isMoreDataAvailable = true
    private var loadMoreListener: OnLoadMoreListener? = null

    private val prettyTime = PrettyTime(Locale.KOREA)

    val apiClient = ApiClient()
    val apiInterface: ApiInterface = apiClient.getApiClient().create(
        ApiInterface::class.java)
    val sharedPreference = SharedPreference()
    private var onWriteCommentClicked: OnWriteCommentClicked? = null
    val email = sharedPreference.getString(context, "email")
    var onPageClickListener: OnPageClickListener? = null
    var onUserClickListener : OnUserClickListener? = null
    var onClickShowAllListener: OnClickShowAllListener? = null
    var onLikeClickListener: OnLikeClickListener? = null









    inner class MyViewHolder(val layout: View) : RecyclerView.ViewHolder(layout){

        val ivPageProfilePhoto: ImageView = layout.findViewById(R.id.pageProfilePhoto)
        val tvPageName: TextView = layout.findViewById(R.id.pageName)
        val tvPageType: TextView = layout.findViewById(R.id.type)
        val tvPageProvince: TextView = layout.findViewById(R.id.province)
        val ivPostImage: ImageView = layout.findViewById(R.id.postImage)
        val ivUserProfilePhoto: ImageView = layout.findViewById(R.id.userProfilePhoto)
        val tvUserName: TextView = layout.findViewById(R.id.userName)
        val tvDescription: TextView = layout.findViewById(R.id.postDescription)
        val tvCountLikes: TextView = layout.findViewById(R.id.countLikes)
        val tvShowAllComents: TextView = layout.findViewById(R.id.showAllComents)
        val tvMainComentUserName: TextView = layout.findViewById(R.id.commentUserName)
        val tvMainCommentContents: TextView = layout.findViewById(R.id.commentContents)
        val ivMyProfile: ImageView = layout.findViewById(R.id.myProfilePhoto)
        val tvWriteComment: TextView = layout.findViewById(R.id.writeComment)
        val tvPostedDate: TextView = layout.findViewById(R.id.postedDate)
        val page: RelativeLayout = layout.findViewById(R.id.page)
        val compass: ImageView = layout.findViewById(R.id.compass)
        val likeBtn: ImageView = layout.findViewById(R.id.likeBtn)
        val commentContainer: RelativeLayout = layout.findViewById(R.id.mainComentContainer)



    }

    class LoadHolder(view: View): RecyclerView.ViewHolder(view){


    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val treeRecyclerRow = inflater.inflate(R.layout.tree_recycler_row, parent, false)
        val loadLayout = inflater.inflate(R.layout.search_recycler_load, parent, false)



        if(viewType == TYPE_POST)
        {
            return MyViewHolder(treeRecyclerRow)
        } else {
            return LoadHolder(
                loadLayout
            )
        }
    }

    override fun getItemViewType(position: Int): Int {

        if(postList[position].kind == "post"){
            return TYPE_POST
        }else{
            return TYPE_LOAD
        }

    }

    override fun getItemCount(): Int {


        return postList.size

    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {



        if(position>= itemCount-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null)
        {
            isLoading = true
            loadMoreListener?.onLoadMore()
        }


        if(getItemViewType(position) == TYPE_POST) {
            holder as MyViewHolder
            val item = postList[position]
            var isLikingPost = item.isLikingPost
            var postLikes = item.postLikes
            var commentCount = item.commentNumber
            val profileUrl = MyApplication.severUrl+profilePhoto
            val pageProfileUrl = MyApplication.severUrl+item.pageProfilePhoto
            val postImageUrl = MyApplication.severUrl+item.postImage
            val userProfilePhotoUrl = MyApplication.severUrl+item.userProfilePhoto
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")






            Glide.with(context).load(profileUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(holder.ivMyProfile.width, holder.ivMyProfile.height)
                .into(holder.ivMyProfile)
            if(pageProfileUrl != "http://18.223.20.219/null")
            Glide.with(context).load(pageProfileUrl)
                .fitCenter()
                .override(holder.ivPageProfilePhoto.width, holder.ivPageProfilePhoto.height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivPageProfilePhoto)

            Glide.with(context).load(postImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivPostImage)

            Glide.with(context).load(userProfilePhotoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(holder.ivUserProfilePhoto.width, holder.ivUserProfilePhoto.width )
                .into(holder.ivUserProfilePhoto)

            holder.tvPageName.text = item.pageName
            holder.tvPageType.text = item.pageType
            holder.tvPageProvince.text = item.pageProvince
            holder.tvUserName.text = item.userName
            holder.tvDescription.text = item.postDescription
            holder.tvMainComentUserName.text = item.mainCommentUserName
            holder.tvMainCommentContents.text = item.mainComment

            if (commentCount!! > 1) {
                holder.tvShowAllComents.text = "댓글 ${commentCount}개 모두 보기"
                holder.tvShowAllComents.visibility = View.VISIBLE
            } else {
                holder.tvShowAllComents.visibility = View.GONE
                holder.tvShowAllComents.text = null
            }
            if (!isLikingPost!!) {
                holder.likeBtn.setImageResource(R.drawable.tree)
            } else {
                holder.likeBtn.setImageResource(R.drawable.tree_selected)
            }
            if (item.commentNumber!!>0) {
                holder.commentContainer.visibility = View.VISIBLE
            } else{
                holder.commentContainer.visibility = View.GONE
            }
            val date = simpleDateFormat.parse(item.postDate!!)
            val ago = prettyTime.format(date)
            holder.tvPostedDate.text = ago
            if(postLikes!! > 0 ){
                holder.tvCountLikes.text = "좋아요 ${item.postLikes}개"
                holder.tvCountLikes.visibility = View.VISIBLE
            }else{
                holder.tvCountLikes.visibility = View.GONE
            }







            holder.page.setOnClickListener {
                onPageClickListener?.onPageClick(position)
            }
            holder.compass.setOnClickListener {
                onPageClickListener?.onPageClick(position)
            }


            holder.ivUserProfilePhoto.setOnClickListener{
                onUserClickListener?.onUserClick(position)
            }
            holder.tvUserName.setOnClickListener{
                onUserClickListener?.onUserClick(position)
            }


            holder.tvShowAllComents.setOnClickListener {
                onClickShowAllListener?.onClickShowAll(position)
            }


            holder.likeBtn.setOnClickListener {
                onLikeClickListener?.onLikeClick(position)
            }


            holder.tvWriteComment.setOnClickListener {
                onWriteCommentClicked?.onWriteCommentClicked(position)
            }



        }


    }






    fun notifyDataChanged(){
        notifyDataSetChanged()
        isLoading = false
    }





    interface OnWriteCommentClicked{

        fun onWriteCommentClicked(position: Int)
    }

    fun addWriteCommentClickListener(listener: OnWriteCommentClicked){

        onWriteCommentClicked = listener

    }



    interface OnLoadMoreListener{
        fun onLoadMore()
    }

    fun addLoadMoreListener(listener: OnLoadMoreListener){

        this.loadMoreListener = listener
    }










    interface OnPageClickListener{
        fun onPageClick(position: Int)
    }
    interface OnUserClickListener{
        fun onUserClick(position: Int)
    }
    interface OnClickShowAllListener{
        fun onClickShowAll(position: Int)
    }
    interface OnLikeClickListener{
        fun onLikeClick(position: Int)
    }
}