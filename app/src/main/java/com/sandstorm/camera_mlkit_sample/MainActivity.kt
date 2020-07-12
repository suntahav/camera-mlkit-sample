package com.sandstorm.camera_mlkit_sample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.sandstorm.camera_mlkit_sample.scratchpad.ScratchPadFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val fragment = ScratchPadFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit()
    }
}