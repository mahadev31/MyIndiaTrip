package com.mytrip.myindiatrip.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.mytrip.myindiatrip.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    lateinit var splashBinding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding=ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        initView()
    }

    private fun initView() {
        Handler().postDelayed(Runnable {
            var i= Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        },1000)
    }
}