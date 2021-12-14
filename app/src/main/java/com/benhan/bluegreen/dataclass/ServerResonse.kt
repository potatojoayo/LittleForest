package com.benhan.bluegreen.dataclass

import com.google.gson.annotations.SerializedName

class ServerResonse {

    @SerializedName("success")
    var success: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("isInserted")
    var isInserted: Boolean? = null

    @SerializedName("insertMessage")
    var insertMessage: String? = null

    @SerializedName("isUpdated")
    var isUpdated: Boolean? = null

    @SerializedName("is_following")
    var isFollowing: Boolean? = null

    @SerializedName("is_liking")
    var isLiking: Boolean? = null

    @SerializedName("profile_photo")
    var profile_photo: String? = null


}