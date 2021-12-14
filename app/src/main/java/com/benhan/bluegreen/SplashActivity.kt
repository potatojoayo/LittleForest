package com.benhan.bluegreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.login.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tree = findViewById<ImageView>(R.id.tree)



        val hd = Handler()
        hd.postDelayed(object: Runnable{
            override fun run(){
                val sharedPreference =
                    SharedPreference()
                val success = sharedPreference.getBoolean(this@SplashActivity, "success")
                if(success!!){

                    startActivity(Intent(application, HomeActivity::class.java))
                    finish()

                }else {

                    startActivity(Intent(application, MainActivity::class.java))
                    finish()
                }
            }
        }, 3000)

    }


    override fun onBackPressed() {

    }


}

