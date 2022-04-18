package com.example.customviewstest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import com.example.customviewstest.databinding.ActivityMainBinding
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomButtons.setListener {
            when(it) {
                BottomButtonAction.POSITIVE -> {
                    binding.bottomButtons.setPositiveButtonText("Update OK")
                    Toast.makeText(this, "positive", Toast.LENGTH_SHORT).show()
                }
                BottomButtonAction.NEGATIVE -> {
                    binding.bottomButtons.setNegativeButtonText("Update Cancel")
                    Toast.makeText(this, "negative", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}