package com.ssr.bmicalculator

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ssr.bmicalculator.data.local.dao.LanguagePreferenceDao
import com.ssr.bmicalculator.data.local.db.AppDatabase
import com.ssr.bmicalculator.data.local.dao.UserDataDao
import com.ssr.bmicalculator.data.local.entity.LanguagePreference
import com.ssr.bmicalculator.data.local.entity.UserData
import com.ssr.bmicalculator.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var intWeight: Int = 55
    private var intAge: Int = 23
    var currentProgress: Int = 0
    var mintProgress: String = "170"
    private var typeofUser: String = "0"
    private lateinit var languageDao: LanguagePreferenceDao
    private lateinit var userDataDao: UserDataDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageDao = AppDatabase.getInstance(application).languagePreferenceDao()
        userDataDao = AppDatabase.getInstance(application).userDataDao()

        // Load saved language preference from Room
        lifecycleScope.launch {
            val savedLanguage = withContext(Dispatchers.IO) {
                languageDao.getPreferredLanguage()?.languageCode
            }
            setLocale(savedLanguage ?: getCurrentLocale())

            // Load saved user data
            val savedUserData = withContext(Dispatchers.IO) {
                userDataDao.getUserData()
            }
            savedUserData?.let {
                typeofUser = it.gender ?: "0"
                intAge = it.age ?: 23
                intWeight = it.weight ?: 55
                currentProgress = it.height?.toIntOrNull() ?: 170
                mintProgress = currentProgress.toString()

                updateUI()
            }

            // Spinner language selector
            val languageSpinner: Spinner? = binding.languageSpinner
            languageSpinner?.setSelection(getLanguagePosition(getCurrentLocale()))

            languageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedLang = when (position) {
                        0 -> "en"
                        1 -> "mk"
                        else -> "en"
                    }

                    if (selectedLang != getCurrentLocale()) {
                        setLocale(selectedLang)
                        // Save the new language preference to Room
                        lifecycleScope.launch(Dispatchers.IO) {
                            val existingPreference = languageDao.getPreferredLanguage()
                            if (existingPreference != null) {
                                languageDao.update(LanguagePreference(1, selectedLang)) // Assuming only one preference row
                            } else {
                                languageDao.insert(LanguagePreference(languageCode = selectedLang))
                            }
                        }
                        recreate()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        val mcurrentAge = binding.currentAge
        val mcurrentWeight = binding.currentWeight
        val mcurrentHeight = binding.currentHeight
        val mincrementAge = binding.incrementAge
        val mdecrementAge = binding.decrementAge
        val mincrementWeight = binding.incrementWeight
        val mdecrementWeight = binding.decrementWeight
        val mseekbarforHeight = binding.seekbarForHeight
        val mMale = binding.male
        val mfemale = binding.female

        mMale?.setOnClickListener {
            mMale.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalefocus)
            mfemale?.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalenotfocus)
            typeofUser = "Male"
            saveUserDataToRoom()
        }

        mfemale?.setOnClickListener {
            mfemale.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalefocus)
            mMale?.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalenotfocus)
            typeofUser = "Female"
            saveUserDataToRoom()
        }

        mseekbarforHeight?.max = 300
        mseekbarforHeight?.progress = currentProgress

        mseekbarforHeight?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentProgress = progress
                mintProgress = currentProgress.toString()
                mcurrentHeight?.text = mintProgress
                saveUserDataToRoom()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mincrementAge?.setOnClickListener {
            intAge++
            binding.currentAge?.text = intAge.toString()
            saveUserDataToRoom()
        }

        mdecrementAge?.setOnClickListener {
            intAge--
            binding.currentAge?.text = intAge.toString()
            saveUserDataToRoom()
        }

        mincrementWeight?.setOnClickListener {
            intWeight++
            binding.currentWeight?.text = intWeight.toString()
            saveUserDataToRoom()
        }

        mdecrementWeight?.setOnClickListener {
            intWeight--
            binding.currentWeight?.text = intWeight.toString()
            saveUserDataToRoom()
        }

        binding.calculateBMI?.setOnClickListener {
            when {
                typeofUser == "0" -> Toast.makeText(applicationContext, R.string.select_gender, Toast.LENGTH_SHORT).show()
                mintProgress == "0" -> Toast.makeText(applicationContext, R.string.select_height, Toast.LENGTH_SHORT).show()
                intAge <= 0 -> Toast.makeText(applicationContext, R.string.invalid_age, Toast.LENGTH_SHORT).show()
                intWeight <= 0 -> Toast.makeText(applicationContext, R.string.invalid_weight, Toast.LENGTH_SHORT).show()
                else -> {
                    saveUserDataToRoom() // Save data before navigating
                    val intent = Intent(this, bmiactivity::class.java)
                    intent.putExtra("gender", typeofUser)
                    intent.putExtra("height", mintProgress)
                    intent.putExtra("weight", intWeight.toString())
                    intent.putExtra("age", intAge.toString())
                    startActivity(intent)
                }
            }
        }
    }

    private fun updateUI() {
        binding.currentAge?.text = intAge.toString()
        binding.currentWeight?.text = intWeight.toString()
        binding.currentHeight?.text = mintProgress

        when (typeofUser) {
            "Male" -> {
                binding.male?.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalefocus)
                binding.female?.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalenotfocus)
            }
            "Female" -> {
                binding.female?.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalefocus)
                binding.male?.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalenotfocus)
            }
            else -> {
                binding.male?.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalenotfocus)
                binding.female?.background = ContextCompat.getDrawable(applicationContext, R.drawable.malefemalenotfocus)
            }
        }
        binding.seekbarForHeight?.progress = currentProgress
    }

    private fun saveUserDataToRoom() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userData = UserData(
                id = 1, // Assuming only one user data entry
                gender = typeofUser,
                age = intAge,
                height = mintProgress,
                weight = intWeight
            )
            val existingUserData = userDataDao.getUserData()
            if (existingUserData != null) {
                userDataDao.update(userData)
            } else {
                userDataDao.insert(userData)
            }
        }
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun getCurrentLocale(): String {
        return Locale.getDefault().language
    }

    private fun getLanguagePosition(lang: String): Int {
        return when (lang) {
            "en" -> 0
            "mk" -> 1
            else -> 0
        }
    }
}


