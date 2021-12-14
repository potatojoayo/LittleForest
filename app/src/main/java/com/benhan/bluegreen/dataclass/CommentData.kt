package com.benhan.bluegreen.dataclass

import com.google.gson.annotations.SerializedName

class CommentData {

    @SerializedName("user_name")
    var user_name: String? = null

    @SerializedName("contents")
    var contents: String? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("likes")
    var likes: Int? = null

    @SerializedName("is_reply")
    var is_reply: Int? = null


    @SerializedName("comment_no")
    var comment_no: Int? = null

    @SerializedName("profile_photo")
    var profile_photo: String? = null

    @SerializedName("is_liked")
    var is_liked: Boolean? = null

    @SerializedName("reply_id")
    var reply_id: Int? = null


}