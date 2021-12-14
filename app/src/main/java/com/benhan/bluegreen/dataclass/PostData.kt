package com.benhan.bluegreen.dataclass

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

class PostData(@SerializedName("kind") var kind: String? = null){

    //place table
    @SerializedName("place_photo")
    var pageProfilePhoto: String? =null
    @SerializedName("place_name")
    var pageName: String? =null
    @SerializedName("place_type")
    var pageType: String? =null
    @SerializedName("place_province")
    var pageProvince: String?=null


    //post table
    @SerializedName("post_path")
    var postImage: String? =null
    @SerializedName("comment_number")
    var commentNumber: Int? =null
    @SerializedName("post_description")
    var postDescription: String? =null
    @SerializedName("post_likes")
    var postLikes: Int? =null
    @SerializedName("post_date")
    var postDate: String? =null
    @SerializedName("post_id")
    var postId: Int? =null
    @SerializedName("post_place_id")
    var pageId: Int? =null


    //user table
    @SerializedName("user_photo")
    var userProfilePhoto: String? =null
    @SerializedName("user_name")
    var userName: String? =null



    //comment table
    @SerializedName("comment_contents")
    var mainComment: String? =null
    @SerializedName("comment_id")
    var mainCommentId: Int? =null
    @SerializedName("comment_user_name")
    var mainCommentUserName: String? =null


    //reply table
    @SerializedName("main_comment_reply_user_name")
    var replyUserName: String? =null
    @SerializedName("main_comment_reply_id")
    var mainCommentReplyId: Int? =null
    @SerializedName("main_comment_reply")
    var mainCommentReply: String? =null


    //likes
    @SerializedName("is_liking_post")
    var isLikingPost: Boolean? = null
    @SerializedName("is_liking_comment")
    var isLikingComment: Boolean? = null

    @SerializedName("total")
    var total: Int? = null
}

