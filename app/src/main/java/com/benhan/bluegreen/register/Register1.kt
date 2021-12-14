package com.benhan.bluegreen.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.benhan.bluegreen.R
import java.text.SimpleDateFormat
import java.util.*

class Register1 : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)



        val nextButton: Button = findViewById(R.id.btnNext)

        nextButton.isEnabled = false







        val tvBirthday = findViewById<TextView>(R.id.select_birthdayRegister)



        val datePicker: DatePicker =  findViewById(R.id.date_picker)




        datePicker.setOnDateChangedListener(object : DatePicker.OnDateChangedListener{

            override fun onDateChanged(
                view: DatePicker?,
                year: Int,
                monthOfYear: Int,
                dayOfMonth: Int
            ) {




                val calendar = Calendar.getInstance()
                 calendar.set(year, monthOfYear , dayOfMonth)
                val simpleDateFormat = object: SimpleDateFormat("yyyy - MM - dd"){}
                val formatedDate = simpleDateFormat.format(calendar.time)

                tvBirthday.text = formatedDate

                tvBirthday.addTextChangedListener(object: TextWatcher{

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {




                    }

                    override fun afterTextChanged(s: Editable?) {



                        if(year < Calendar.getInstance().get(Calendar.YEAR) - 8)
                        {
                            nextButton.isEnabled = true


                            nextButton.setBackgroundResource(R.drawable.button_shape)
                            val enabledTextColor = ContextCompat.getColor(this@Register1,
                                R.color.background
                            )
                            nextButton.setTextColor(enabledTextColor)


                        }
                        else if (year > Calendar.getInstance().get(Calendar.YEAR) - 8) {

                            nextButton.isEnabled = false

                            nextButton.setBackgroundResource(R.drawable.button_shape_disable)
                            val disabledTextColor = ContextCompat.getColor(this@Register1,
                                R.color.disabled
                            )
                            nextButton.setTextColor(disabledTextColor)
                        }
                    }

                })










            }
            })






        fun onClicked() {

            val intent = Intent(this@Register1, Register2::class.java)
            intent.putExtra("birthday", tvBirthday.text.toString() )
            startActivity(intent)
            finish()
        }





        nextButton.setOnClickListener{

            onClicked()
        }




    }

















}
