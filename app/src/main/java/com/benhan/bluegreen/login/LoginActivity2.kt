package com.benhan.bluegreen.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.benhan.bluegreen.HomeActivity
import com.benhan.bluegreen.R
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.dataclass.User
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback

class LoginActivity2 : AppCompatActivity() {



    private lateinit var apiInterface: ApiInterface

//    val prefConfig = PrefConfig(this)

    private val apiClient = ApiClient()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)



        apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)


        val emailFromRegister4 = intent.getStringExtra("email")


        val et_email = findViewById<EditText>(R.id.etEmailLogin)
        val et_password = findViewById<EditText>(R.id.etPasswordLogin)


        et_password.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event?.action == MotionEvent.ACTION_DOWN){
                    et_password.text = null
                }
                return false
            }

        })







        //
        fun performLogin(){

            val password = et_password.text.toString()
            val email = et_email.text.toString()

            val call: Call<User> = this.apiInterface.performUserLogin(email, password)
            call.enqueue(object : Callback<User>{
                override fun onFailure(call: Call<User>, t: Throwable) {

                    Log.d("에러", t.message)

                }

                override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {

                    if (response.body()?.success == true){

                        val sharedPreference =
                            SharedPreference()
                        sharedPreference.setString(this@LoginActivity2, "email", response.body()?.email!!)
                        sharedPreference.setString(this@LoginActivity2, "password", response.body()?.password!!)
                        sharedPreference.setString(this@LoginActivity2, "name", response.body()?.name!!)
                        sharedPreference.setString(this@LoginActivity2, "birthday", response.body()?.birthday!!)
                        sharedPreference.setBoolean(this@LoginActivity2, "success", response.body()?.success!!)
                        sharedPreference.setString(this@LoginActivity2, "actualName", response.body()?.actualname!!)
                        sharedPreference.setString(this@LoginActivity2, "job", response.body()?.job!!)
                        sharedPreference.setString(this@LoginActivity2, "introduction", response.body()?.introduction!!)
                        if(!response.body()?.profilephoto.isNullOrEmpty()) {
                            sharedPreference.setString(
                                this@LoginActivity2,
                                "profilePhoto",
                                response.body()?.profilephoto!!
                            )
                        }
                        val intent = Intent(this@LoginActivity2, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else{

                        Toast.makeText(this@LoginActivity2, "이메일 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()

                    }

                }


            })


        }





        //



        et_password.inputType = InputType.TYPE_CLASS_TEXT
        et_password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        et_password.transformationMethod = PasswordTransformationMethod()
        et_password.typeface = ResourcesCompat.getFont(this,
            R.font.nixgonfonts_b
        )


        if (!emailFromRegister4.isNullOrEmpty()){

            et_email.text = Editable.Factory.getInstance().newEditable(emailFromRegister4)
        }

        val btn_login = findViewById<Button>(R.id.btnLogIn)
        val tvOnOtherWay:TextView = findViewById(R.id.tvLoginOtherWay)

        btn_login.isEnabled = false





        et_password.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {

                if (et_password.text.isNotEmpty()){btn_login.isEnabled = true}

                if(btn_login.isEnabled){

                    btn_login.setBackgroundResource(R.drawable.button_shape)
                    val enabledTextColor = ContextCompat.getColor(this@LoginActivity2,
                        R.color.disabled
                    )
                    btn_login.setTextColor(enabledTextColor)
                    et_password.typeface = ResourcesCompat.getFont(this@LoginActivity2,
                        R.font.dearsunshine
                    )

                } else {

                    btn_login.setBackgroundResource(R.drawable.button_shape_disable)
                    val disabledTextColor = ContextCompat.getColor(this@LoginActivity2,
                        R.color.background
                    )
                    btn_login.setTextColor(disabledTextColor)
                    et_password.typeface = ResourcesCompat.getFont(this@LoginActivity2,
                        R.font.nixgonfonts_b
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

        })







        tvOnOtherWay.setOnClickListener {
            onOtherWayClicked()
        }


        btn_login.setOnClickListener(object: View.OnClickListener{

            override fun onClick(v: View){



                performLogin()


            }


        } )





    }



    private fun onOtherWayClicked(){

        val intent = Intent(this, LoginActivity::class.java)

        startActivity(intent)

    }



}




