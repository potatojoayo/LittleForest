package com.benhan.bluegreen.dataclass

import com.google.gson.annotations.SerializedName

class BellData(@SerializedName("kind")
var kind: String? = null) {

    @SerializedName("user_name")
    var user_name: String? = null

    @SerializedName("date")
    var date: String? =null

    @SerializedName("post_id")
    var post_id: Int? = null

    @SerializedName("type")
    var type: String?= null

    @SerializedName("profile_photo")
    var profile_photo: String? = null

    @SerializedName("post_image")
    var post_image: String? = null

    @SerializedName("total")
    var total: Int? = null



}