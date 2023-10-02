package com.example.customviewstest.presentation//package com.example.customviewstest.presentation
//
//import android.content.Intent
//import android.opengl.Visibility
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import com.example.customviewstest.BottomButtonAction
//import com.example.customviewstest.Cell
//import com.example.customviewstest.CustomFieldsTest
//import com.example.customviewstest.CustomViewSurfaceViewTest
//import com.example.customviewstest.databinding.ActivityMainBinding
//import kotlin.random.Random
//
//class com.example.customviewstest.presentation.MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//
//    private var player = 1
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setBigGrid()
//        setContentView(binding.root)
//        binding.bottomButtons.setPositiveButtonText("Random cells")
//        binding.bottomButtons.setNegativeButtonText("Next View")
//
//        setClickListenerForBottomButtonsView()
//        setClickListenerForCustomView()
//    }
//
//    private fun setClickListenerForCustomView() {
//        binding.customViewTest.actionListener = { row, column ->
//            val field = binding.customViewTest.field
//
//            when (player) {
//                1 -> {
//                    field.setCell(row, column, Cell.PLAYER_1)
//                    player++
//                }
//                2 -> {
//                    field.setCell(row, column, Cell.PLAYER_2)
//                    player--
//                }
//            }
//        }
//    }
//
//
//    private fun setClickListenerForBottomButtonsView() {
//        binding.bottomButtons.setListener {
//            when (it) {
//                BottomButtonAction.POSITIVE -> {
//                    setRandomGrid()
//                    setContentToCells()
//                }
//                BottomButtonAction.NEGATIVE -> {
//                    startActivity(Intent(this, SurfaceActivity::class.java))
//                }
//            }
//        }
//    }
//
//    private fun setRandomGrid() {
//        binding.customViewTest.field = CustomFieldsTest(
//            Random.nextInt(3, 10),
//            Random.nextInt(3, 10)
//        )
//    }
//
//    private fun setContentToCells() {
//        val field = binding.customViewTest.field
//
//        for (row in 0 until field.rows) {
//            for (column in 0 until field.columns) {
//                if (Random.nextBoolean()) field.setCell(row, column, Cell.PLAYER_1)
//                else field.setCell(row, column, Cell.PLAYER_2)
//            }
//        }
//    }
//
//    private fun setBigGrid() {
//        binding.customViewTest.field = CustomFieldsTest(10, 10)
//    }
//
//    private fun setGreedLikeItsFirstLab() {
//       // binding.customViewTest.setGreedLikeItsFirstLab()
//    }
//}

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var juliaFractalView: JuliaFractalView
    private lateinit var zoomEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = androidx.constraintlayout.widget.ConstraintLayout(this)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        juliaFractalView = JuliaFractalView(this)
        layout.addView(juliaFractalView)

        zoomEditText = EditText(this)
        zoomEditText.hint = "Enter zoom value"
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layout.addView(zoomEditText, layoutParams)

        setContentView(layout)

        zoomEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val zoomValue = s.toString().toDoubleOrNull()
                if (zoomValue != null) {
                    juliaFractalView.updateZoom(zoomValue)
                }
            }
        })
    }

    inner class JuliaFractalView(context: Context) : View(context) {
        private lateinit var fractalBitmap: Bitmap
        private var constant: Complex = Complex(-0.7, 0.27015)
        private var zoom: Double = 1.0

        init {
            generateFractal()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawBitmap(fractalBitmap, 0f, 0f, null)
        }

        fun updateZoom(newZoom: Double) {
            zoom = newZoom
            generateFractal()
            invalidate()
        }

        private fun generateFractal() {
            val dm  = context.resources.displayMetrics
            val width = dm.widthPixels
            val height = dm.heightPixels
            fractalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val zx = 1.5 * (x - width / 2) / (0.5 * width * zoom)
                    val zy = (y - height / 2) / (0.5 * height * zoom)
                    var z = Complex(zx, zy)
                    var iterations = 0

                    while (z.magnitude() < 4 && iterations < 200) {
                        z = z * z + constant
                        iterations++
                    }

                    val color = Color.rgb(
                        iterations % 8 * 32,
                        iterations % 16 * 16,
                        iterations % 32 * 8
                    )
                    fractalBitmap.setPixel(x, y, color)
                }
            }
        }
    }

    data class Complex(val real: Double, val imaginary: Double) {
        operator fun plus(other: Complex): Complex {
            return Complex(real + other.real, imaginary + other.imaginary)
        }

        operator fun times(other: Complex): Complex {
            return Complex(
                real * other.real - imaginary * other.imaginary,
                real * other.imaginary + imaginary * other.real
            )
        }

        fun magnitude(): Double {
            return Math.sqrt(real * real + imaginary * imaginary)
        }
    }
}