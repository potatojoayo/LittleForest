package com.benhan.bluegreen.dataclass

import com.google.gson.annotations.SerializedName

class PostImageData(@SerializedName("kind")var kind: String? = null) {


    @SerializedName("post_img")
    var postImg: String? = null
    @SerializedName("id")
    var postId: Int? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("distance")
    var distance: Double? = null
}