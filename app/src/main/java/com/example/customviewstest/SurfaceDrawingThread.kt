package com.example.customviewstest

import android.graphics.*
import android.util.Log
import android.view.SurfaceHolder
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.random.Random

enum class Sides {
    LEFT, RIGHT, TOP, BOTTOM
}

class SurfaceDrawingThread(private val surfaceHolder: SurfaceHolder) : Thread() {

    var runThread = true
    var motion = false
    private var frameTime by Delegates.notNull<Long>()

    private var sideFrom: Sides = Sides.TOP
    private lateinit var sideTo: Sides

    private var isFirstIteration = true
    private val circlePainter = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }

    private var centerX by Delegates.notNull<Int>()
    private var centerY by Delegates.notNull<Int>()
    private var differenceX by Delegates.notNull<Int>()
    private var differenceY by Delegates.notNull<Int>()
    private var relationSynchronization by Delegates.notNull<Double>()
    private var probability by Delegates.notNull<Int>()
    private var runtimeProbability by Delegates.notNull<Int>()

    override fun run() {
        val canvas = surfaceHolder.lockCanvas()
        centerX = canvas.width / 2
        centerY = canvas.height / 2
        val pointFrom = Point(centerX, centerY)
        val pointTo = Point(0, 0)
        resetRelationSynchronization(pointTo, pointFrom)
        surfaceHolder.unlockCanvasAndPost(canvas)

        while (runThread) {
            val frameStartTime = System.nanoTime()
            if (isFirstIteration) {
                surfaceHolder.lockCanvas()
                setPointTo(canvas, pointTo)
                drawObject(centerX.toFloat(), centerY.toFloat(), canvas)
                isFirstIteration = false
                surfaceHolder.unlockCanvasAndPost(canvas)
                return
            }
            surfaceHolder.lockCanvas()

            if (centerX == pointTo.x && centerY == pointTo.y) {
                pointFrom.x = pointTo.x
                pointFrom.y = pointTo.y
                setPointTo(canvas, pointTo)
                while (sideFrom == sideTo) setPointTo(canvas, pointTo)
                resetColor()
                resetRelationSynchronization(pointTo, pointFrom)
                sideFrom = sideTo
            }
            drawObject(centerX.toFloat(), centerY.toFloat(), canvas)
            resetCircleCoordinates(pointTo)

            surfaceHolder.unlockCanvasAndPost(canvas)
            frameTime = (System.nanoTime() - frameStartTime) / 1000000
            if (frameTime < MAX_FRAME_TIME) {
                try {
                    sleep(MAX_FRAME_TIME - frameTime)
                } catch (e: InterruptedException) { }
            }
        }
    }

    private fun setPointTo(canvas: Canvas, pointTo: Point) {

        with(pointTo) {
            when (Random.nextInt(1, 5)) {

                1 -> {
                    x = Random.nextInt(0, canvas.width)
                    y = 0
                    sideTo = Sides.TOP
                }
                2 -> {
                    x = Random.nextInt(canvas.width)
                    y = Random.nextInt(0, canvas.height)
                    sideTo = Sides.RIGHT
                }
                3 -> {
                    x = Random.nextInt(0, canvas.width)
                    y = canvas.height
                    sideTo = Sides.BOTTOM
                }
                4 -> {
                    x = 0
                    y = Random.nextInt(0, canvas.height)
                    sideTo = Sides.LEFT
                }
            }
        }
        Log.d("ffdfdfds", pointTo.toString())
    }

    private fun resetCircleCoordinates(pointTo: Point) {
        if (differenceX > differenceY) {
            if (centerX > pointTo.x) centerX--
            else if (centerX < pointTo.x) centerX++

            probability = (relationSynchronization * 100).toInt()
            runtimeProbability = Random.nextInt(0, 101)
            if (runtimeProbability < probability) {
                if (centerY > pointTo.y) centerY--
                else if (centerY < pointTo.y) centerY++
            }
        }

        if (differenceX < differenceY) {
            probability = (relationSynchronization * 100).toInt()
            runtimeProbability = Random.nextInt(0, 101)
            if (runtimeProbability <= probability) {
                if (centerX > pointTo.x) centerX--
                else if (centerX < pointTo.x) centerX++
            }

            if (centerY > pointTo.y) centerY--
            else if (centerY < pointTo.y) centerY++
        }
    }

    private fun resetRelationSynchronization(pointTo: Point, pointFrom: Point) {
        differenceX = max(pointTo.x, pointFrom.x) - min(pointTo.x, pointFrom.x)
        differenceY = max(pointTo.y, pointFrom.y) - min(pointTo.y, pointFrom.y)
        relationSynchronization = min(differenceX.toDouble(), differenceY.toDouble()) / max(
            differenceX.toDouble(),
            differenceY.toDouble()
        )
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