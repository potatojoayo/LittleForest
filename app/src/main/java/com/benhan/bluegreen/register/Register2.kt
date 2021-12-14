package com.benhan.bluegreen.register

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.benhan.bluegreen.R
import com.benhan.bluegreen.dataclass.User
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback

class Register2 : AppCompatActivity() {


    companion object{

        fun isValidEmail(email: CharSequence): Boolean{

            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }


    }

    val apiClient = ApiClient()
    lateinit var apiInterface: ApiInterface

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)



        window.decorView.importantForAutofill= View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS


        apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)



        val checkSign = findViewById<ImageView>(R.id.check)

        val warningSign = findViewById<ImageView>(R.id.warning)



        checkSign.visibility = View.INVISIBLE
        warningSign.visibility = View.INVISIBLE


        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)




        val birthday = intent.getStringExtra("birthday")


        val etEmail = findViewById<EditText>(R.id.etRegister)


        val emailText = etEmail.text



        etEmail.requestFocus()


        val btnNext = findViewById<Button>(R.id.btnNext)

        btnNext.isEnabled = false


        fun btnStyle() {

            if (btnNext.isEnabled) {

                btnNext.setBackgroundResource(R.drawable.button_shape)
                val enabledTextColor = ContextCompat.getColor(this@Register2,
                    R.color.background
                )
                btnNext.setTextColor(enabledTextColor)

            } else if (!btnNext.isEnabled) {


                btnNext.isEnabled = false
                btnNext.setBackgroundResource(R.drawable.button_shape_disable)
                val disabledTextColor = ContextCompat.getColor(this@Register2,
                    R.color.disabled
                )
                btnNext.setTextColor(disabledTextColor)

            }

        }















        etEmail.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {



            }


            override fun afterTextChanged(s: Editable?) {


                if (emailText.isNotEmpty()){

                    btnNext.isEnabled = true
                    btnStyle()

                    if (isValidEmail(
                            emailText
                        )
                    ) {


                        fun performEmailCheck(){


                            val email = etEmail.text.toString()

                            val call: Call<User> = this@Register2.apiInterface.performEmailCheck(email)
                            call.enqueue(object : Callback<User> {
                                override fun onFailure(call: Call<User>, t: Throwable) {

                                    Log.d("에러", t.message)

                                }

                                override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {


                                    val success: Boolean = response.body()!!.success!!

                                    if (!success){

                                        checkSign.visibility = View.VISIBLE
                                        warningSign.visibility = View.INVISIBLE

                                        btnNext.isEnabled = true
                                        btnStyle()
                                    } else{

                                        checkSign.visibility = View.INVISIBLE
                                        warningSign.visibility = View.VISIBLE

                                        btnNext.isEnabled = false
                                        btnStyle()

                                        btnNext.setOnClickListener {

                                        }

                                    }


                                }


                            })


                        }

                        performEmailCheck()



                        btnNext.setOnClickListener {
                            fun onClick() {

                                val intent = Intent(this@Register2, Register3::class.java)

                                intent.putExtra("birthday", birthday)
                                intent.putExtra("email", emailText.toString())
                                startActivity(intent)
                            }
                            onClick()
                        }

                    }
                    else {



                        checkSign.visibility = View.INVISIBLE
                        warningSign.visibility = View.INVISIBLE

                        btnNext.setOnClickListener{


                            Toast.makeText(this@Register2, "이메일주소를 확인해주세요", Toast.LENGTH_SHORT).show()


                        }
                    }

                }

                else{

                    checkSign.visibility = View.INVISIBLE
                    warningSign.visibility = View.INVISIBLE

                    btnNext.isEnabled = false

                    btnStyle()

                }


            }





        })





    }





}
