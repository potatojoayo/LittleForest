package com.benhan.bluegreen.plus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.benhan.bluegreen.R

class PlusActivity : AppCompatActivity() {






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plus)



//
//
//        val colorEnabled = ContextCompat.getColor(this, R.color.background)
//        val colorDisabled = ContextCompat.getColor(this, R.color.navi)
//
//
//        val gallery = findViewById<TextView>(R.id.gallery)
//        val photo = findViewById<TextView>(R.id.photo)
//        val video = findViewById<TextView>(R.id.video)
//
//
//        val fragmentManager = supportFragmentManager
//        val transaction = fragmentManager.beginTransaction()
//
//
//
//
//        fun clickHandler(view: TextView){
//
//            view.setOnClickListener {
//
//                val fTransaction = fragmentManager.beginTransaction()
//
//                when(view.id){
//
//                    R.id.gallery -> {
//
//                        fTransaction.replace(R.id.plusFrame, fragmentGallery)
//                        gallery.setTextColor(colorEnabled)
//                        photo.setTextColor(colorDisabled)
//                        video.setTextColor(colorDisabled)
//
//                    }
//
//                    R.id.photo -> {
//
//                        fTransaction.replace(R.id.plusFrame, fragmentPhoto)
//                        gallery.setTextColor(colorDisabled)
//                        photo.setTextColor(colorEnabled)
//                        video.setTextColor(colorDisabled)
//
//                    }
//
//                    R.id.video -> {
//
//                        fTransaction.replace(R.id.plusFrame, fragmentVideo)
//                        gallery.setTextColor(colorDisabled)
//                        photo.setTextColor(colorDisabled)
//                        video.setTextColor(colorEnabled)
//
//                    }
//
//
//                }
//
//                fTransaction.commit()
//
//
//            }
//
//
//        }
//
//        clickHandler(gallery)
//        clickHandler(photo)
//        clickHandler(video)
//
//
//
//        //
//
//
//
//
//
//


    }

}



