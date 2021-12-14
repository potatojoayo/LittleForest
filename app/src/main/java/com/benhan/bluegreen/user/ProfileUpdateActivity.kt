package com.benhan.bluegreen.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.benhan.bluegreen.R
import com.benhan.bluegreen.dataclass.User
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileUpdateActivity : AppCompatActivity(){

    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)

    private var profilePhotoUri : String? = null
    private var userUpdateProfilePhoto: ImageView? = null
    private var actualname: String? = null
    var name: String? = null
    private var job: String? = null
    private var introduction: String? = null
    val sharedPreference = SharedPreference()
    var profile_photo: String? = null
    var etActName: EditText? = null
    var etName: TextView? = null
    var etJob: EditText? = null
    var etIntroduce: EditText? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_update)

        etActName = findViewById(R.id.etActname)
        etName = findViewById(R.id.etName)
        etJob = findViewById(R.id.etJob)
        etIntroduce = findViewById(R.id.etIntroduce)









        val email = sharedPreference.getString(this, "email")!!
        val tvChangeProfilePhoto = findViewById<TextView>(R.id.changeProfilePhoto)
        userUpdateProfilePhoto = findViewById(R.id.userProfileUpdateProfilePhoto)
        val done = findViewById<TextView>(R.id.profileUpdateDone)


        actualname = sharedPreference.getString(this, "actualName")
        name = sharedPreference.getString(this, "name")
        job = sharedPreference.getString(this, "job")
        introduction = sharedPreference.getString(this, "introduction")
        profile_photo = sharedPreference.getString(this, "profilePhoto")



        profilePhotoUri = MyApplication.severUrl+profile_photo






        done.setOnClickListener {
            val textActName = etActName?.text.toString()
            val textName = etName?.text.toString()
            val textJob = etJob?.text.toString()
            val textIntroduce = etIntroduce?.text.toString()
            val updateProfile:Call<User> = this.apiInterface.updateProfile(textActName,
                textName,
                textJob,
                textIntroduce,
                email)


            updateProfile.enqueue(object : Callback<User>{
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("프로필업데이트", t.message)
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {


                    sharedPreference.setString(this@ProfileUpdateActivity, "actualName", textActName )
                    sharedPreference.setString(this@ProfileUpdateActivity, "introduction", textIntroduce )
                    sharedPreference.setString(this@ProfileUpdateActivity, "job", textJob )
                    finish()
                }


            })

            hideKeyboard(this)


        }
        val cancel = findViewById<TextView>(R.id.profileUpdateCancel)









        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {

                startActivity(Intent(this@ProfileUpdateActivity, ProfilePhotoPicker::class.java))


            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                Toast.makeText(this@ProfileUpdateActivity,"권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
            }


        }





        tvChangeProfilePhoto.setOnClickListener {
            TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("프로필 사진을 바꾸기 위해서는 쓰기 권한이 필요해요")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
        }

        userUpdateProfilePhoto!!.setOnClickListener {

            TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("프로필 사진을 바꾸기 위해서는 쓰기 권한이 필요해요")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
        }




        cancel.setOnClickListener {

            this.finish()
            hideKeyboard(this)

        }

        etActName?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    etActName?.clearFocus()
                    hideKeyboard(this@ProfileUpdateActivity)
                }
                return false
            }
        })

        etName?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    etActName?.clearFocus()
                    hideKeyboard(this@ProfileUpdateActivity)
                }
                return false
            }
        })

        etJob?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    etActName?.clearFocus()
                    hideKeyboard(this@ProfileUpdateActivity)
                }
                return false
            }
        })

        etIntroduce?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    etActName?.clearFocus()
                    hideKeyboard(this@ProfileUpdateActivity)
                }
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()

        profile_photo = sharedPreference.getString(this, "profilePhoto")
        profilePhotoUri = MyApplication.severUrl+profile_photo
        val tmpProfilePhoto = sharedPreference.getString(this,"tmpProfilePhoto")
        if(tmpProfilePhoto!!.isNotBlank()){
            Glide.with(this@ProfileUpdateActivity)
                .load(tmpProfilePhoto)
                .override(userUpdateProfilePhoto!!.width, userUpdateProfilePhoto!!.height)
                .into(userUpdateProfilePhoto!!)
        }else {
            Glide.with(this@ProfileUpdateActivity)
                .load(profilePhotoUri)
                .override(userUpdateProfilePhoto!!.width, userUpdateProfilePhoto!!.height)
                .into(userUpdateProfilePhoto!!)
        }
        etActName?.setText(actualname)
        etName?.text = name
        etJob?.setText(job)
        etIntroduce?.setText(introduction)
    }



    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}



