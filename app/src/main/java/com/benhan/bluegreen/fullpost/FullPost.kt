package com.benhan.bluegreen.fullpost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.benhan.bluegreen.user.OtherUser
import com.benhan.bluegreen.place.PlacePage
import com.benhan.bluegreen.R
import com.benhan.bluegreen.comment.Comment
import com.benhan.bluegreen.dataclass.CommentData
import com.benhan.bluegreen.dataclass.PostData
import com.benhan.bluegreen.dataclass.ServerResonse
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.Functions
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pnikosis.materialishprogress.ProgressWheel
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import org.ocpsoft.prettytime.PrettyTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class FullPost : AppCompatActivity() {

    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)
    val sharedPreference = SharedPreference()


    var pageProfilePhoto: String? = null
    var pageName: String? = null
    var pageType: String? = null
    var pageProvince: String? = null
    var postImage: String? = null
    var userProfilePhoto: String? = null
    var userName: String? = null
    var description: String? = null
    var countLikes: Int? = null
    var mainComentUserName: String? = null
    var mainCommentContents: String? = null

    var postedDate: String? = null
    var commentNumber: Int? = null
    var isLikingPost: Boolean? = null
    var isLikingComment: Boolean? = null
    var mainCommentId: Int? = null
    var pageId: Int? = null
    var myName: String? = null
    var ago: String? = null


    var functions: Functions? = null

    var container: RelativeLayout? = null
    var progressWheel: ProgressWheel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_full_post)


        container = findViewById(R.id.container)
        container?.visibility = View.GONE
        progressWheel = findViewById(R.id.progress_wheel)

        functions = Functions(this)
        val backgroundColor = ContextCompat.getColor(
            this,
            R.color.background
        )
        val naviColor = ContextCompat.getColor(
            this,
            R.color.navi
        )

        val place_or_user_name = intent.getStringExtra("place_or_user_name")
        val post_id = intent.getIntExtra("post_id", 0)


        val myEmail = sharedPreference.getString(this, "email")

        val myProfilePhoto = sharedPreference.getString(this, "profilePhoto")
        myName = sharedPreference.getString(this, "name")


        /*                      views                   */

        val ivBack: ImageView = findViewById(R.id.ivBack)
        val tvName: TextView = findViewById(R.id.name)
        val ivPageProfilePhoto: ImageView = findViewById(R.id.pageProfilePhoto)
        val tvPageName: TextView = findViewById(R.id.pageName)
        val tvPageType: TextView = findViewById(R.id.type)
        val tvPageProvince = findViewById<TextView>(R.id.province)
        val ivPostImage: ImageView = findViewById(R.id.postImage)
        val ivUserProfilePhoto = findViewById<ImageView>(R.id.userProfilePhoto)
        val tvUserName = findViewById<TextView>(R.id.userName)
        val tvDescription = findViewById<TextView>(R.id.postDescription)
        val tvCountLikes = findViewById<TextView>(R.id.countLikes)
        val tvShowAllComents = findViewById<TextView>(R.id.showAllComents)
        val tvMainComentUserName = findViewById<TextView>(R.id.commentUserName)
        val ivMainComentLike = findViewById<ImageView>(R.id.mainCommentLike)
        val tvMainCommentContents = findViewById<TextView>(R.id.commentContents)
        val ivMyProfile = findViewById<ImageView>(R.id.myProfilePhoto)
        val etWriteComment = findViewById<EditText>(R.id.writeComment)
        val tvPostedDate = findViewById<TextView>(R.id.postedDate)
        val uploadComment = findViewById<TextView>(R.id.uploadComment)
        val page = findViewById<RelativeLayout>(R.id.page)
        val compass = findViewById<ImageView>(R.id.compass)
        val likeBtn = findViewById<ImageView>(R.id.likeBtn)
        val commentContainer = findViewById<RelativeLayout>(R.id.mainComentContainer)
        val tvDelete = findViewById<TextView>(R.id.delete)

        if (myName == place_or_user_name) {
            tvDelete.visibility = View.VISIBLE
        } else {
            tvDelete.visibility = View.GONE
        }


        etWriteComment.clearFocus()

        fun getSinglePost(email: String, post_id: Int) {

            val call: Call<PostData> = apiInterface.getSinglePost(email, post_id)
            call.enqueue(object : Callback<PostData> {
                override fun onFailure(call: Call<PostData>, t: Throwable) {

                    Log.d("문제", t.message)

                }

                override fun onResponse(call: Call<PostData>, response: Response<PostData>) {

                    val res = response.body()

                    pageProfilePhoto = res?.pageProfilePhoto
                    pageName = res?.pageName
                    pageType = res?.pageType
                    pageProvince = res?.pageProvince
                    userProfilePhoto = res?.userProfilePhoto
                    userName = res?.userName
                    description = res?.postDescription
                    countLikes = res?.postLikes
                    mainComentUserName = res?.mainCommentUserName
                    mainCommentContents = res?.mainComment
                    commentNumber = res?.commentNumber
                    isLikingComment = res?.isLikingComment
                    isLikingPost = res?.isLikingPost
                    mainCommentId = res?.mainCommentId
                    postImage = res?.postImage
                    mainCommentId = res?.mainCommentId
                    pageId = res?.pageId
                    postedDate = res?.postDate


                    tvDelete.setOnClickListener {

                        MyApplication.isChanged = true
                        val delete: Call<ServerResonse> =
                            apiInterface.delete(post_id, postImage!!, pageId!!)
                        delete.enqueue(object : Callback<ServerResonse> {
                            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {
                                Log.d("삭제", t.message)
                                finish()
                            }

                            override fun onResponse(
                                call: Call<ServerResonse>,
                                response: Response<ServerResonse>
                            ) {
                                finish()
                            }


                        })
                    }

                    if (!postedDate.isNullOrEmpty()) {
                        val prettyTime = PrettyTime(Locale.KOREA)
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                        val date = simpleDateFormat.parse(postedDate)
                        ago = prettyTime.format(date)
                        tvPostedDate.text = ago
                    }


                    val pageProfileUri = MyApplication.severUrl + pageProfilePhoto
                    val postImageUri = MyApplication.severUrl + postImage
                    val userProfileUri = MyApplication.severUrl + userProfilePhoto
                    val myProfilePhotoUri = MyApplication.severUrl + myProfilePhoto
                    Glide.with(this@FullPost).load(pageProfileUri)
                        .override(ivPageProfilePhoto.width, ivPageProfilePhoto.height)
                        .into(ivPageProfilePhoto)
                    Glide.with(this@FullPost).load(postImageUri)
                        .fitCenter()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivPostImage)
                    Glide.with(this@FullPost).load(userProfileUri)
                        .override(ivUserProfilePhoto.width, ivUserProfilePhoto.height)
                        .into(ivUserProfilePhoto)
                    Glide.with(this@FullPost).load(myProfilePhotoUri)
                        .override(ivMyProfile.width, ivMyProfile.height)
                        .into(ivMyProfile)



                    tvName.text = place_or_user_name
                    tvPageName.text = pageName
                    tvPageType.text = pageType
                    tvPageProvince.text = pageProvince
                    tvUserName.text = userName
                    tvDescription.text = description

                    tvShowAllComents.text = "댓글 ${commentNumber}개 모두 보기"
                    tvMainComentUserName.text = mainComentUserName
                    tvMainCommentContents.text = mainCommentContents



                    if (countLikes!! == 0) {
                        tvCountLikes.visibility = View.GONE
                        tvCountLikes.text = null
                    } else {
                        tvCountLikes.visibility = View.VISIBLE
                        tvCountLikes.text = "좋아요 ${countLikes}개"
                    }

                    if (commentNumber!! < 2) {
                        tvShowAllComents.visibility = View.GONE
                        tvShowAllComents.text = null
                    } else {
                        tvShowAllComents.text = "댓글 ${commentNumber}개 모두 보기"
                        tvShowAllComents.visibility = View.VISIBLE
                        tvShowAllComents.setOnClickListener {
                            val intent = Intent(this@FullPost, Comment::class.java)
                            intent.putExtra("post_user_name", userName)
                            intent.putExtra("post_description", description)
                            intent.putExtra("post_user_profile", userProfilePhoto)
                            intent.putExtra("post_date", postedDate)
                            intent.putExtra("post_id", post_id!!)
                            startActivity(intent)
                        }
                    }


                    if (!isLikingComment!!) {
                        ivMainComentLike.setImageResource(R.drawable.tree)
                    } else {
                        ivMainComentLike.setImageResource(R.drawable.tree_selected)

                    }



                    ivMainComentLike.setOnClickListener {
                        if (!isLikingComment!!) {
                            likeComment(myEmail!!, mainCommentId!!, myName!!)
                            ivMainComentLike.setImageResource(R.drawable.tree_selected)
                            isLikingComment = true
                        } else {
                            unlikeComment(myEmail!!, mainCommentId!!)
                            ivMainComentLike.setImageResource(R.drawable.tree)
                            isLikingComment = false
                        }
                    }

                    page.setOnClickListener {
                        openPage()
                    }
                    compass.setOnClickListener {
                        openPage()
                    }

                    fun startOtherUserPage() {
                        val intent = Intent(this@FullPost, OtherUser::class.java)
                        intent.putExtra("otherUserName", userName)
                        startActivity(intent)
                    }
                    ivUserProfilePhoto.setOnClickListener {


                        startOtherUserPage()


                    }
                    tvUserName.setOnClickListener {

                        startOtherUserPage()
                    }

                    if (tvMainCommentContents.text.isNullOrBlank()) {
                        commentContainer.visibility = View.GONE
                    } else {
                        commentContainer.visibility = View.VISIBLE

                    }

                    if (!isLikingPost!!) {
                        likeBtn.setImageResource(R.drawable.tree)
                    } else {
                        likeBtn.setImageResource(R.drawable.tree_selected)
                    }

                    likeBtn.setOnClickListener {
                        if (!isLikingPost!!) {
                            likePost(myEmail!!, post_id, myName!!)
                            countLikes = countLikes!! + 1
                            likeBtn.setImageResource(R.drawable.tree_selected)
                            isLikingPost = true
                            if (countLikes == 0) {
                                tvCountLikes.visibility = View.GONE
                                tvCountLikes.text = null
                            } else {
                                tvCountLikes.visibility = View.VISIBLE
                                tvCountLikes.text = "좋아요 ${countLikes}개"
                            }
                        } else {
                            unlikePost(myEmail!!, post_id)
                            countLikes = countLikes!! - 1
                            likeBtn.setImageResource(R.drawable.tree)
                            isLikingPost = false
                            if (countLikes == 0) {
                                tvCountLikes.visibility = View.GONE
                                tvCountLikes.text = null
                            } else {
                                tvCountLikes.visibility = View.VISIBLE
                                tvCountLikes.text = "좋아요 ${countLikes}개"
                            }
                        }
                    }

                    container?.visibility = View.VISIBLE
                    progressWheel?.visibility = View.GONE
                }


            })

        }


        getSinglePost(myEmail!!, post_id)





        ivBack.setOnClickListener {
            finish()
        }



        tvShowAllComents.setOnClickListener {
            val intent = Intent(this, Comment::class.java)
            intent.putExtra("post_user_name", userName)
            intent.putExtra("post_description", description)
            intent.putExtra("post_user_profile", userProfilePhoto)
            intent.putExtra("post_date", postedDate)
            intent.putExtra("post_id", post_id)
            startActivity(intent)
        }



        etWriteComment.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(
                v: TextView?,
                actionId: Int,
                event: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (etWriteComment.text.isNotBlank())
                        commentContainer.visibility = View.VISIBLE
                    tvMainComentUserName.text =
                        sharedPreference.getString(this@FullPost, "name")
                    val call: Call<ArrayList<CommentData>> = this@FullPost.apiInterface
                        .writeComment(
                            myEmail,
                            post_id,
                            myName!!,
                            etWriteComment.text.toString()
                        )
                    if (etWriteComment.text.isNotEmpty() && etWriteComment.text.isNotBlank()) {
                        call.clone().enqueue(object : Callback<ArrayList<CommentData>> {
                            override fun onFailure(
                                call: Call<ArrayList<CommentData>>,
                                t: Throwable
                            ) {

                            }

                            override fun onResponse(
                                call: Call<ArrayList<CommentData>>,
                                response: Response<ArrayList<CommentData>>
                            ) {

                            }


                        })

                        commentNumber = commentNumber!! + 1

                        if (commentNumber!! > 1) {
                            tvShowAllComents.visibility = View.VISIBLE
                            tvShowAllComents.text = "댓글 ${commentNumber!!}개 모두 보기"
                        }
                        tvMainCommentContents.text =
                            etWriteComment.text.toString()
                    }
                    etWriteComment.text = null
                    etWriteComment.clearFocus()
                    UIUtil.hideKeyboard(this@FullPost)


                }
                return false
            }

        })
        etWriteComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etWriteComment.text.isEmpty()) {
                    uploadComment.setTextColor(naviColor)
                } else {
                    uploadComment.setTextColor(backgroundColor)
                    uploadComment.setOnClickListener {


                        commentContainer.visibility = View.VISIBLE
                        tvMainComentUserName.text =
                            sharedPreference.getString(this@FullPost, "name")
                        val call: Call<java.util.ArrayList<CommentData>> =
                            this@FullPost.apiInterface
                                .writeComment(
                                    myEmail,
                                    post_id,
                                    myName!!,
                                    etWriteComment.text.toString()
                                )
                        if (etWriteComment.text.isNotEmpty() && etWriteComment.text.isNotBlank()) {
                            call.clone()
                                .enqueue(object : Callback<java.util.ArrayList<CommentData>> {
                                    override fun onFailure(
                                        call: Call<java.util.ArrayList<CommentData>>,
                                        t: Throwable
                                    ) {

                                    }

                                    override fun onResponse(
                                        call: Call<java.util.ArrayList<CommentData>>,
                                        response: Response<java.util.ArrayList<CommentData>>
                                    ) {

                                    }


                                })

                            commentNumber = commentNumber!! + 1

                            if (commentNumber!! > 1) {
                                tvShowAllComents.visibility = View.VISIBLE
                                tvShowAllComents.text = "댓글 ${commentNumber!!}개 모두 보기"
                            }
                            tvMainCommentContents.text =
                                etWriteComment.text.toString()
                        }
                        etWriteComment.text = null
                        etWriteComment.clearFocus()
                        UIUtil.hideKeyboard(this@FullPost)


                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }


        })



        ivPostImage.setOnClickListener {

            etWriteComment.clearFocus()

            functions?.hideKeyboard(etWriteComment)
        }
    }


    fun likeComment(email: String, commentId: Int, user_name: String) {

        val call: Call<ServerResonse> = apiInterface.likeComment(email, commentId, user_name)
        call.enqueue(object : Callback<ServerResonse> {
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ServerResonse>,
                response: Response<ServerResonse>
            ) {


            }
        })

    }

    fun unlikeComment(email: String, commentId: Int) {

        val call: Call<ServerResonse> = apiInterface.unLikeComment(email, commentId)
        call.enqueue(object : Callback<ServerResonse> {
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ServerResonse>,
                response: Response<ServerResonse>
            ) {

            }
        })


    }

    fun openPage() {

        val intent = Intent(this, PlacePage::class.java)


        intent.putExtra("placeName", pageName)
        intent.putExtra("placeId", pageId)
        intent.putExtra("placePhoto", pageProfilePhoto)
        intent.putExtra("placeType", pageType)
        intent.putExtra("placeProvince", pageProvince)
        startActivity(intent)

    }

    fun likePost(email: String, postId: Int, user_name: String) {

        val call: Call<ServerResonse> = apiInterface.likePost(email, postId, user_name)
        call.enqueue(object : Callback<ServerResonse> {
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ServerResonse>,
                response: Response<ServerResonse>
            ) {


            }
        })


    }

    fun unlikePost(email: String, postId: Int) {

        val call: Call<ServerResonse> = apiInterface.unLikePost(email, postId)
        call.enqueue(object : Callback<ServerResonse> {
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ServerResonse>,
                response: Response<ServerResonse>
            ) {

            }
        })

    }


}

