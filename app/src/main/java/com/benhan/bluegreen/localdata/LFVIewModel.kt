package com.benhan.bluegreen.localdata

import androidx.lifecycle.ViewModel
import com.benhan.bluegreen.dataclass.PostData

class LFVIewModel: ViewModel() {


    var postDataList = ArrayList<PostData>()
    var otherUserPostData = ArrayList<PostData>()


}