package com.benhan.bluegreen.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.benhan.bluegreen.R
import java.util.regex.Pattern

class Register3 : AppCompatActivity() {


    companion object{

        fun isValidPassword(password: CharSequence): Boolean {

            val passwordPattern = "(?=.*\\d{1,50})(?=.*[~`!@#\$%\\^&*()-+=]{1,50})(?=.*[a-zA-Z]{1,50}).{6,50}\$"
            return Pattern.compile(passwordPattern).matcher(password).matches()


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register3)

        window.decorView.importantForAutofill= View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS


        val birthday = intent.getStringExtra("birthday")
        val email = intent.getStringExtra("email")
        val etPassword = findViewById<EditText>(R.id.etPasswordRegister)
        val passwordChar = etPassword.text
        val btnNext = findViewById<Button>(R.id.btnNext)


        btnNext.isEnabled = false

        fun btnStyle() {

            if (btnNext.isEnabled) {

                btnNext.setBackgroundResource(R.drawable.button_shape)
                val enabledTextColor = ContextCompat.getColor(this@Register3,
                    R.color.background
                )
                btnNext.setTextColor(enabledTextColor)

            } else if (!btnNext.isEnabled) {


                btnNext.isEnabled = false
                btnNext.setBackgroundResource(R.drawable.button_shape_disable)
                val disabledTextColor = ContextCompat.getColor(this@Register3,
                    R.color.disabled
                )
                btnNext.setTextColor(disabledTextColor)

            }

        }

        etPassword.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {




            }

            override fun afterTextChanged(s: Editable?) {


                if (passwordChar.toString().length > 5){

                    btnNext.isEnabled = true
                    btnStyle()

                    if(isValidPassword(
                            passwordChar
                        )
                    ){

                        btnNext.setOnClickListener {

                            fun onClick(){

                                val intent = Intent(this@Register3, Register4::class.java)
                                intent.putExtra("birthday", birthday)
                                intent.putExtra("email", email)
                                intent.putExtra("password", passwordChar.toString())
                                startActivity(intent)


                            }

                            onClick()

                        }

                    }

                    else{



                        btnNext.setOnClickListener {
                            Toast.makeText(
                                this@Register3,
                                "영문, 숫자, 특수문자를 모두 쓰셨나요?",
                                Toast.LENGTH_SHORT
                            ).show()


                        }
                    }


                }

                else {

                    etPassword.typeface = ResourcesCompat.getFont(this@Register3,
                        R.font.washyourhand
                    )
                    btnNext.isEnabled = false
                    btnStyle()
                }

            }

            })





        }





    }

