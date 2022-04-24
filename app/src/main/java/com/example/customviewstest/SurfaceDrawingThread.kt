package com.example.customviewstest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.SurfaceHolder
import kotlin.random.Random

class SurfaceDrawingThread(private val surfaceHolder: SurfaceHolder) : Thread() {

    var runThread = true

    private var downDirection = true
    private var isFirstIteration = true
    private val circlePainter = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }

    override fun run() {
        val canvas = surfaceHolder.lockCanvas()
        var centerX = canvas.width / 2
        var centerY = canvas.height / 2

        val circleRect =
            RectF(
                centerX + DESIRED_CELL_SIZE + STROKE_WIDTH / 2,
                centerY + DESIRED_CELL_SIZE + STROKE_WIDTH / 2,
                centerX - DESIRED_CELL_SIZE - STROKE_WIDTH / 2,
                centerY - DESIRED_CELL_SIZE - STROKE_WIDTH / 2
            )
        surfaceHolder.unlockCanvasAndPost(canvas)

        while (runThread) {
            if (isFirstIteration) {
                surfaceHolder.lockCanvas()
                drawObject(centerX.toFloat(), centerY.toFloat(), canvas)
                isFirstIteration = false
                surfaceHolder.unlockCanvasAndPost(canvas)
                return
            }
            surfaceHolder.lockCanvas()
            drawObject(centerX.toFloat(), centerY.toFloat(), canvas)

            if (downDirection) {
                centerX++
                centerY++
                circleRect.right++
                circleRect.left++

                if (circleRect.left == canvas.width.toFloat()) {
                    downDirection = false
                    resetColor()
                }
            } else {
                centerX--
                centerY--
                circleRect.right--
                circleRect.left--

                if (circleRect.right == 0f) {
                    downDirection = true
                    resetColor()
                }
            }

            surfaceHolder.unlockCanvasAndPost(canvas)
            sleep(DELAY_FOR_30_FPS)
        }
    }

    private fun drawObject(x: Float, y: Float, canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawCircle(
            x,
            y,
            DESIRED_CELL_SIZE,
            circlePainter
        )
    }

    private fun resetColor() {
        circlePainter.color = Color.argb(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )
    }

    companion object {
        const val DESIRED_CELL_SIZE = 50f
        const val DELAY_FOR_30_FPS: Long = 30
        const val STROKE_WIDTH = 70f
    }
}