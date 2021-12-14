package com.benhan.bluegreen.dataclass

import com.google.gson.annotations.SerializedName

class UserPageData {

    @SerializedName("post_number")
    val postNumber: Int? = null

    @SerializedName("follower_number")
    val followerNumber: Int? = null

    @SerializedName("like_number")
    val likeNumber: Int? = null

}