package com.example.customviewstest.presentation

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.customviewstest.BottomButtonAction
import com.example.customviewstest.Cell
import com.example.customviewstest.CustomFieldsTest
import com.example.customviewstest.CustomViewSurfaceViewTest
import com.example.customviewstest.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var player = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomButtons.setPositiveButtonText("Random cells")
        binding.bottomButtons.setNegativeButtonText("Next View")

        setClickListenerForBottomButtonsView()
        setClickListenerForCustomView()
    }

    private fun setClickListenerForCustomView() {
        binding.customViewTest.actionListener = { row, column ->
            val field = binding.customViewTest.field

            when (player) {
                1 -> {
                    field.setCell(row, column, Cell.PLAYER_1)
                    player++
                }
                2 -> {
                    field.setCell(row, column, Cell.PLAYER_2)
                    player--
                }
            }
        }
    }


    private fun setClickListenerForBottomButtonsView() {
        binding.bottomButtons.setListener {
            when (it) {
                BottomButtonAction.POSITIVE -> {
                    setRandomGrid()
                    setContentToCells()
                }
                BottomButtonAction.NEGATIVE -> {
                    startActivity(Intent(this, SurfaceActivity::class.java))
                }
            }
        }
    }

    private fun setRandomGrid() {
        binding.customViewTest.field = CustomFieldsTest(
            Random.nextInt(3, 10),
            Random.nextInt(3, 10)
        )
    }

    private fun setContentToCells() {
        val field = binding.customViewTest.field

        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                if (Random.nextBoolean()) field.setCell(row, column, Cell.PLAYER_1)
                else field.setCell(row, column, Cell.PLAYER_2)
            }
        }
    }
}