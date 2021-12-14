package com.benhan.bluegreen.dataclass

import com.google.gson.annotations.SerializedName

class PlacePageData {

    @SerializedName("post_number")
    val postNumber: Int? = null

    @SerializedName("follower_number")
    val followerNumber: Int? = null

    @SerializedName("like_number")
    val likeNumber: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("photo")
    var photo: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("province")
    var province: String? = null

}