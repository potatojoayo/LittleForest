package com.benhan.bluegreen.dataclass

import com.google.gson.annotations.SerializedName

class User {


    @SerializedName("success")
    var success: Boolean? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("birthday")
    var birthday: String? = null

    @SerializedName("actual_name")
    var actualname: String? = null

    @SerializedName("job")
    var job:String? = null

    @SerializedName("introduction")
    var introduction: String? = null

    @SerializedName("profile_photo")
    var profilephoto: String? = null


    @SerializedName("post_number")
    val postNumber: Int? = null

    @SerializedName("follow_number")
    val followerNumber: Int? = null

    @SerializedName("like_number")
    val likeNumber: Int? = null


}