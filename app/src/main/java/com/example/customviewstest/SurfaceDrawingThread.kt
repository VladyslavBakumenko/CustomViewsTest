package com.example.customviewstest

import android.graphics.*
import android.util.Log
import android.view.SurfaceHolder
import kotlin.properties.Delegates
import kotlin.random.Random

class SurfaceDrawingThread(private val surfaceHolder: SurfaceHolder) : Thread() {

    var runThread = true
    private var frameTime by Delegates.notNull<Long>()

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
            val frameStartTime = System.nanoTime();
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
                if (circleRect.left.toInt() == canvas.width) {
                    downDirection = false
                    resetColor()
                }
            } else {
                centerX--
                centerY--
                circleRect.right--
                circleRect.left--

                if (circleRect.right.toInt() == 0) {
                    downDirection = true
                    resetColor()
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas)

            frameTime = (System.nanoTime() - frameStartTime) / 1000000
            if (frameTime < MAX_FRAME_TIME)
            {
                try {
                    sleep(MAX_FRAME_TIME - frameTime)
                } catch (e: InterruptedException) {

                }
            }
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
        const val STROKE_WIDTH = 5f
        const val MAX_FRAME_TIME = (1000 / 30)
    }
}

