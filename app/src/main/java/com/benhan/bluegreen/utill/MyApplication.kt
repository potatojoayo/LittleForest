package com.benhan.bluegreen.utill

import android.app.Application

open class MyApplication: Application() {

    init {
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: MyApplication
        const val severUrl = "http://15.165.70.243/"
        var isChanged = false
        var isProfileUpdated = false
    }


}