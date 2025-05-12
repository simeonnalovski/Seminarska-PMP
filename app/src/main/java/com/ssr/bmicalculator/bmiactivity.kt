package com.ssr.bmicalculator

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ssr.bmicalculator.databinding.ActivityBmiactivityBinding
import java.text.DecimalFormat
import androidx.core.graphics.drawable.toDrawable


class bmiactivity : AppCompatActivity() {
    private lateinit var binding: ActivityBmiactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiactivity)

        // Inflate the layout and bind the views using ViewBinding
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.title = "Result"
        val drawable = Color.parseColor("#1e1d1d").toDrawable()
        actionBar?.setBackgroundDrawable(drawable)

        val gender = intent.getStringExtra("gender")
        val height = intent.getStringExtra("height")
        val weight = intent.getStringExtra("weight")
        val age = intent.getStringExtra("age")

        var intWeight = weight?.toFloat()
        var intHeight = height?.toFloat()

        intHeight = intHeight?.div(100)
        val BMI = intWeight?.div((intHeight!! *intHeight))
        val decimalFormat = DecimalFormat("#.##")
        val intBMI = decimalFormat.format(BMI).toString()

        if (BMI != null) {
            if(BMI<=18.4){
                binding.bmiValue.text = intBMI
                binding.bmiCategory.text = getString(R.string.underweight)
                binding.imageView.setImageResource(R.drawable.warn)
            }
            else if(BMI in 18.5..24.99){
                binding.bmiValue.text = intBMI
                binding.bmiCategory.text = getString(R.string.hweight)
            }
            else if(BMI in 25.0..29.99){
                binding.imageView.setImageResource(R.drawable.warn)
                binding.bmiValue.text = intBMI
                binding.bmiCategory.text = getString(R.string.overweight)
            }
            else if(BMI in 30.0..34.99){
                binding.imageView.setImageResource(R.drawable.declined)
                binding.bmiValue.text = intBMI
                binding.bmiCategory.text = getString(R.string.obsClass1)
            }
            else if(BMI in 35.0..39.99){
                binding.imageView.setImageResource(R.drawable.declined)
                binding.bmiValue.text = intBMI
                binding.bmiCategory.text = getString(R.string.obsClass2)
            }
            else if(BMI >= 40.0){
                binding.imageView.setImageResource(R.drawable.declined)
                binding.bmiValue.text = intBMI
                binding.bmiCategory.text = getString(R.string.exObsClass3)
            }
        }
        binding.genderText.text = gender
        // Access the view with ID "recalculateBMI"
        val recalculateBMIButton = binding.recalculateBMI

        recalculateBMIButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}