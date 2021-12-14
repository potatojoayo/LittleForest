package com.benhan.bluegreen.register

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.benhan.bluegreen.HomeActivity
import com.benhan.bluegreen.R
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.dataclass.User
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback

class Register4 : AppCompatActivity() {

    val TAG: String = "로그"

    private lateinit var apiInterface : ApiInterface
//    val prefConfig = PrefConfig(this)

    val apiClient = ApiClient()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register4)



        val etNickname = findViewById<EditText>(R.id.etNickNameRegister)
        val nicknameChar = etNickname.text

        //

        val birthday = intent.getStringExtra("birthday")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        val checkSign = findViewById<ImageView>(R.id.check)

        val warningSign = findViewById<ImageView>(R.id.warning)
        val showAgreement = findViewById<TextView>(R.id.showAgreement)

        showAgreement.setOnClickListener {
            startActivity(Intent(this, RegisterAgreement::class.java))
        }



        fun performLogin(){


            val call: Call<User> = apiInterface.performUserLogin(email, password)
            call.enqueue(object : Callback<User>{
                override fun onFailure(call: Call<User>, t: Throwable) {

                    Log.d("에러", t.message)

                }

                override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {

                    if (response.body()?.success == true){

                        val sharedPreference =
                            SharedPreference()
                        sharedPreference.setString(this@Register4, "email", response.body()?.email!!)
                        sharedPreference.setString(this@Register4, "password", response.body()?.password!!)
                        sharedPreference.setString(this@Register4, "name", response.body()?.name!!)
                        sharedPreference.setString(this@Register4, "birthday", response.body()?.birthday!!)
                        sharedPreference.setBoolean(this@Register4, "success", response.body()?.success!!)
                        sharedPreference.setString(this@Register4, "actualName", response.body()?.actualname!!)
                        sharedPreference.setString(this@Register4, "job", response.body()?.job!!)
                        sharedPreference.setString(this@Register4, "introduction", response.body()?.introduction!!)
                        if(!response.body()?.profilephoto.isNullOrEmpty()) {
                            sharedPreference.setString(
                                this@Register4,
                                "profilePhoto",
                                response.body()?.profilephoto!!
                            )
                        }
                        val intent = Intent(this@Register4, HomeActivity::class.java)
                        startActivity(intent)

                    } else{

                        Toast.makeText(this@Register4, "이메일 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()

                    }

                }


            })


        }

        checkSign.visibility = View.INVISIBLE
        warningSign.visibility = View.INVISIBLE





        //


        apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)


        fun performRegistration() {

            val name = etNickname.text.toString()
            val call: Call<User> = this.apiInterface.performRegistration(email, password, name, birthday)

            call.enqueue(object : Callback<User>{
                override fun onFailure(call: Call<User>, t: Throwable) {

                }

                override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {
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
                    hideKeyboard(this@Register4)

                    if (response.body()?.success == true) {

                        Toast.makeText(this@Register4, "가입완료!", Toast.LENGTH_SHORT).show()
                        performLogin()



                    }
                }


            })


        }






        //button

        val btnRegister = findViewById<Button>(R.id.btnRegisterRegister)
        btnRegister.isEnabled = false

        fun btnStyle() {

            if (btnRegister.isEnabled) {

                btnRegister.setBackgroundResource(R.drawable.button_shape)
                val enabledTextColor = ContextCompat.getColor(this@Register4,
                    R.color.background
                )
                btnRegister.setTextColor(enabledTextColor)

            } else if (!btnRegister.isEnabled) {


                btnRegister.isEnabled = false
                btnRegister.setBackgroundResource(R.drawable.button_shape_disable)
                val disabledTextColor = ContextCompat.getColor(this@Register4,
                    R.color.disabled
                )
                btnRegister.setTextColor(disabledTextColor)

            }


        }

        btnStyle()

        //


        etNickname.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {



            }

            override fun afterTextChanged(s: Editable?) {

                if(nicknameChar.isNotEmpty()){


                    checkSign.visibility = View.INVISIBLE
                    warningSign.visibility = View.INVISIBLE


                    if (nicknameChar.length > 1){


                        btnRegister.isEnabled = true
                        btnStyle()



                        Log.d(TAG, "Register4 - afterTextChanged() called")

                        btnRegister.setOnClickListener(object : View.OnClickListener{

                            override fun onClick(v: View?) {
                                
                                Log.d(TAG, "Register4 - onClick() called")








                                fun nameCheck(){


                                    val name = etNickname.text.toString()

                                    val call: Call<User> = this@Register4.apiInterface.nameCheck(name)
                                    call.enqueue(object : Callback<User> {
                                        override fun onFailure(call: Call<User>, t: Throwable) {

                                            Log.d("에러", t.message)

                                        }

                                        override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {


                                            val success: Boolean = response.body()!!.success!!

                                            if (!success){

                                                checkSign.visibility = View.VISIBLE
                                                warningSign.visibility = View.INVISIBLE
                                                performRegistration()

                                            } else{

                                                checkSign.visibility = View.INVISIBLE
                                                warningSign.visibility = View.VISIBLE




                                            }


                                        }


                                    })


                                }

                                nameCheck()



                            }

                        })

                    }

                } else{

                    btnRegister.isEnabled = false
                    btnStyle()
                }

            }



        })


    }

}
