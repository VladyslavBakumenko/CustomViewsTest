package com.example.customviewstest.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.customviewstest.CustomViewSurfaceViewTest

class SurfaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CustomViewSurfaceViewTest(this))
        Toast.makeText(this, "Tab to screen for stars", Toast.LENGTH_LONG).show()

    }


}