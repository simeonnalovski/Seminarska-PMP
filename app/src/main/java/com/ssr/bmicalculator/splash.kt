package com.ssr.bmicalculator

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*

class splash : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //To hide action bar
        supportActionBar?.hide()
        hideSystemUI()
        window.statusBarColor = getColor(R.color.lightBlack)

        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000) // Delay for 2 seconds (2000 milliseconds)
            // After the delay, perform the desired action
            val intent = Intent(this@splash, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // method to hide system UI when splash screen is shown
    private fun hideSystemUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.let { controller ->
//                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.systemBars())
//                controller.systemBarsBehavior =
//                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            }
//        } else{
//            @Suppress("DEPRECATION")
//            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//        }
    }
}
