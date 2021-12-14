package com.benhan.bluegreen.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.R
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.dataclass.PostData
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class HomeRecyclerAdapter(
    val context: Context,
    val activity: Activity,
    private var postList: ArrayList<PostData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_POST = 0
        const val TYPE_LOAD = 1
        const val NUM_CACHED_VIEWS = 5

    }

    private var isLoading = false
    var isMoreDataAvailable = true
    private var loadMoreListener: OnLoadMoreListener? = null

    private val prettyTime = PrettyTime(Locale.KOREA)

    val apiClient = ApiClient()
    val apiInterface: ApiInterface = apiClient.getApiClient().create(
        ApiInterface::class.java
    )
    val sharedPreference = SharedPreference()
    private var onWriteCommentClicked: OnWriteCommentClicked? = null
    val email = sharedPreference.getString(context, "email")
    var onPageClickListener: OnPageClickListener? = null
    var onUserClickListener: OnUserClickListener? = null
    var onClickShowAllListener: OnClickShowAllListener? = null
    var onLikeClickListener: OnLikeClickListener? = null


    inner class MyViewHolder(val layout: View) : RecyclerView.ViewHolder(layout) {

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


        fun bind(postData: PostData) {


            val pageProfileUrl = MyApplication.severUrl + postData.pageProfilePhoto
            val postImageUrl = MyApplication.severUrl + postData.postImage
            val userProfilePhotoUrl = MyApplication.severUrl + postData.userProfilePhoto
            var isLikingPost = postData.isLikingPost
            var postLikes = postData.postLikes
            var commentCount = postData.commentNumber
            val profilePhoto = sharedPreference.getString(context, "profilePhoto")
            val profileUrl = MyApplication.severUrl + profilePhoto
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

            if (pageProfileUrl != "http://18.223.20.219/null")
                Glide.with(context).load(pageProfileUrl)
                    .fitCenter()
                    .override(ivPageProfilePhoto.width, ivPageProfilePhoto.height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPageProfilePhoto)

            Glide.with(context).load(profileUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(ivMyProfile.width, ivMyProfile.height)
                .into(ivMyProfile)

            Glide.with(context).load(postImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivPostImage)

            Glide.with(context).load(userProfilePhotoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(ivUserProfilePhoto.width, ivUserProfilePhoto.width)
                .into(ivUserProfilePhoto)

            tvPageName.text = postData.pageName
            tvPageType.text = postData.pageType
            tvPageProvince.text = postData.pageProvince
            tvUserName.text = postData.userName
            tvDescription.text = postData.postDescription
            tvMainComentUserName.text = postData.mainCommentUserName
            tvMainCommentContents.text = postData.mainComment

            if (commentCount!! > 1) {
                tvShowAllComents.text = "댓글 ${commentCount}개 모두 보기"
                tvShowAllComents.visibility = View.VISIBLE
            } else {
                tvShowAllComents.visibility = View.GONE
                tvShowAllComents.text = null
            }
            if (!isLikingPost!!) {
                likeBtn.setImageResource(R.drawable.tree)
            } else {
                likeBtn.setImageResource(R.drawable.tree_selected)
            }
            if (postData.commentNumber!! > 0) {
                commentContainer.visibility = View.VISIBLE
            } else {
                commentContainer.visibility = View.GONE
            }
            val date = simpleDateFormat.parse(postData.postDate!!)
            val ago = prettyTime.format(date)
            tvPostedDate.text = ago
            if (postLikes!! > 0) {
                tvCountLikes.text = "좋아요 ${postData.postLikes}개"
                tvCountLikes.visibility = View.VISIBLE
            } else {
                tvCountLikes.visibility = View.GONE
            }

        }

        fun setInterfaces(position: Int) {

            page.setOnClickListener {
                onPageClickListener?.onPageClick(position)
            }
            compass.setOnClickListener {
                onPageClickListener?.onPageClick(position)
            }
            ivUserProfilePhoto.setOnClickListener {
                onUserClickListener?.onUserClick(position)
            }
            tvUserName.setOnClickListener {
                onUserClickListener?.onUserClick(position)
            }
            tvShowAllComents.setOnClickListener {
                onClickShowAllListener?.onClickShowAll(position)
            }
            likeBtn.setOnClickListener {
                onLikeClickListener?.onLikeClick(position)
            }
            tvWriteComment.setOnClickListener {
                onWriteCommentClicked?.onWriteCommentClicked(position)
            }
        }


    }




    private val asyncLayoutInflater = AsyncLayoutInflater(context)
    private val cachedViews = Stack<View>()


    init {
        //Create some views asynchronously and add them to our stack
        for (i in 0..NUM_CACHED_VIEWS) {
            asyncLayoutInflater.inflate(R.layout.tree_recycler_row, null) { view, layoutRes, viewGroup ->
                cachedViews.push(view)
            }
        }
    }



    class LoadHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val treeRecyclerRow = if(cachedViews.isEmpty()) {
            inflater.inflate(R.layout.tree_recycler_row, parent, false)
        } else {
            cachedViews.pop().also { it.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) }
        }
        val loadLayout = inflater.inflate(R.layout.search_recycler_load, parent, false)



        return if (viewType == TYPE_POST) {
            MyViewHolder(treeRecyclerRow)
        } else {
            LoadHolder(
                loadLayout
            )
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (postList[position].kind == "post") {
            TYPE_POST
        } else {
            TYPE_LOAD
        }

    }

    override fun getItemCount(): Int {


        return postList.size

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true
            loadMoreListener?.onLoadMore()
        }


        if (getItemViewType(position) == TYPE_POST) {
            holder as MyViewHolder
            val item = postList[position]
            holder.bind(item)
            holder.setInterfaces(position)
        }


    }


    fun notifyDataChanged() {
        notifyDataSetChanged()
        isLoading = false
    }


    interface OnWriteCommentClicked {

        fun onWriteCommentClicked(position: Int)
    }

    fun addWriteCommentClickListener(listener: OnWriteCommentClicked) {

        onWriteCommentClicked = listener

    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun addLoadMoreListener(listener: OnLoadMoreListener) {

        this.loadMoreListener = listener
    }

    interface OnPageClickListener {
        fun onPageClick(position: Int)
    }

    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    interface OnClickShowAllListener {
        fun onClickShowAll(position: Int)
    }

    interface OnLikeClickListener {
        fun onLikeClick(position: Int)
    }
}