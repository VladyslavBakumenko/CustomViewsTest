package com.example.customviewstest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import kotlin.random.Random

class SurfaceDrawingThread(private val surfaceHolder: SurfaceHolder) :
    Thread() {

    private val circlePainter = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 30f
    }

    private var downDirection = true
    private var isFirstIteration = true
    var rednering = true

    override fun run() {
        val canvas = surfaceHolder.lockCanvas()
        var centerX = canvas.width / 2
        var centerY = canvas.height / 2
        surfaceHolder.unlockCanvasAndPost(canvas)

        while (rednering) {
            if (isFirstIteration) {
                surfaceHolder.lockCanvas()
                drawObject(centerX.toFloat(), centerY.toFloat(), canvas)
                isFirstIteration = false
                surfaceHolder.unlockCanvasAndPost(canvas)
                return
            }
            surfaceHolder.lockCanvas()
            if (centerX == 0 || centerY == 0 || centerX == canvas.width || centerY == canvas.height) {
                circlePainter.color = Color.argb(
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256)
                )

                if (centerX == canvas.width) downDirection = false
                else if (centerX == 0) downDirection = true
            }

            drawObject(centerX.toFloat(), centerY.toFloat(), canvas)
            if (downDirection) {
                centerX++
                centerY++
            } else {
                centerX--
                centerY--
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

    companion object {
        const val DESIRED_CELL_SIZE = 50f
        const val DELAY_FOR_30_FPS: Long = 30
    }
}