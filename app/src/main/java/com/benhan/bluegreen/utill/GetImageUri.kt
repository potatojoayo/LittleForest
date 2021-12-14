package com.benhan.bluegreen.utill

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.benhan.bluegreen.dataclass.PhotoVO
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GetImageUri(context: Context) {



    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN
    )

   val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

    val cursor = context.contentResolver.query(

        uri,
        projection,
        null,
        null,
        sortOrder
    )

    fun dateToTimestamp(day:Int, month:Int, year:Int): Long
    = SimpleDateFormat("yyyy.MM.dd").let{
        formatter -> formatter.parse("$year.$month.$day")?.time ?: 0
    }

    fun getImageUri(): ArrayList<PhotoVO>{

        val photoList = arrayListOf<PhotoVO>()

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn =
                it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            val displayName =
                it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (it.moveToNext()){
                val id = it.getLong(idColumn)
                val dateTaken = Date(it.getLong(dateTakenColumn))
                val displayName = it.getString(displayName)
                val contentUris = ContentUris.withAppendedId(uri, id)


//                imageUriList.add(contentUris)

                val photoVO =
                    PhotoVO(contentUris, false)

                photoList.add(photoVO)






            }
        }



        return photoList



    }






}