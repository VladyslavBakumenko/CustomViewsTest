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

    private lateinit var sideFrom: Sides
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

    private var topCircleSide = Point()
    private var bottomCircleSide = Point()
    private var rightCircleSide = Point()
    private var leftCircleSide = Point()

    private var canvasIsOpen = false

    override fun run() {
        try {
            val canvas = surfaceHolder.lockCanvas()
            centerX = canvas.width / 2
            centerY = canvas.height / 2
            val pointTo = Point(0, 0)
            setPointTo(canvas, pointTo)
            resetRelationSynchronization(pointTo)
            surfaceHolder.unlockCanvasAndPost(canvas)

            while (runThread) {
                val frameStartTime = System.nanoTime()
                if (isFirstIteration) {
                    surfaceHolder.lockCanvas()
                    resetCircleSidesCoordinates()
                    drawObject(centerX.toFloat(), centerY.toFloat(), canvas)
                    isFirstIteration = false
                    surfaceHolder.unlockCanvasAndPost(canvas)
                    return
                }
                surfaceHolder.lockCanvas()
                resetAllIfCircleTouchToSide(pointTo, canvas)

                if (canvasIsOpen) {
                    surfaceHolder.lockCanvas()
                    canvasIsOpen = false
                }
                Log.d("fdfdfd", "3")

                resetCircleSidesCoordinates()
                resetCircleCoordinates(pointTo)
                drawObject(centerX.toFloat(), centerY.toFloat(), canvas)

                surfaceHolder.unlockCanvasAndPost(canvas)
                frameTime = (System.nanoTime() - frameStartTime) / 1000000
                if (frameTime < MAX_FRAME_TIME) {
                    try {
                        sleep(MAX_FRAME_TIME - frameTime)
                       // sleep(1)
                    } catch (e: InterruptedException) {
                    }
                }
            }
        } catch (e: IllegalStateException) {
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
                    x = 0
                    y = Random.nextInt(0, canvas.height)
                    sideTo = Sides.LEFT
                }
                3 -> {
                    x = Random.nextInt(0, canvas.width)
                    y = canvas.height
                    sideTo = Sides.BOTTOM
                }
                4 -> {
                    x = canvas.width
                    y = Random.nextInt(0, canvas.height)
                    sideTo = Sides.RIGHT
                }
                else -> Log.d("nothing", "nothing")
            }
        }
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
            if (runtimeProbability < probability) {
                if (centerX > pointTo.x) centerX--
                else if (centerX < pointTo.x) centerX++
            }

            if (centerY > pointTo.y) centerY--
            else if (centerY < pointTo.y) centerY++
        }
    }

    private fun resetCircleSidesCoordinates() {
        topCircleSide.x = centerX
        topCircleSide.y = (centerY - CIRCLE_RADIUS).toInt()

        bottomCircleSide.x = centerX
        bottomCircleSide.y = (centerY + CIRCLE_RADIUS).toInt()

        rightCircleSide.x = (centerX + CIRCLE_RADIUS).toInt()
        rightCircleSide.y = centerY

        leftCircleSide.x = (centerX - CIRCLE_RADIUS).toInt()
        leftCircleSide.y = centerY
    }

    private fun resetRelationSynchronization(pointTo: Point) {
        differenceX = max(pointTo.x, centerX) - min(pointTo.x, centerX)
        differenceY = max(pointTo.y, centerY) - min(pointTo.y, centerY)
        relationSynchronization = min(differenceX.toDouble(), differenceY.toDouble()) / max(
            differenceX.toDouble(),
            differenceY.toDouble()
        )
    }

    private fun resetAllIfCircleTouchToSide(pointTo: Point, canvas: Canvas) {
        if (checkCircleToTouchInSide(canvas)) {

            sideFrom = sideTo
            setPointTo(canvas, pointTo)
            while (sideFrom == sideTo) setPointTo(canvas, pointTo)
            resetColor()
            resetRelationSynchronization(pointTo)

            while (checkCircleToTouchInSide(canvas)) {
                resetCircleCoordinates(pointTo)
                resetCircleSidesCoordinates()
                canvas.drawColor(Color.WHITE)
                drawObject(centerX.toFloat(), centerY.toFloat(), canvas)
                surfaceHolder.unlockCanvasAndPost(canvas)
                canvasIsOpen = true
            }
        }
    }

    private fun checkCircleToTouchInSide(canvas: Canvas): Boolean {
        var result = false
        if (topCircleSide.x in 0..canvas.width && topCircleSide.y == 0
            || bottomCircleSide.x in 0..canvas.width && bottomCircleSide.y == canvas.height
            || rightCircleSide.x == canvas.width && rightCircleSide.y in 0..canvas.height
            || leftCircleSide.x == 0 && leftCircleSide.y in 0..canvas.height
        ) result = true

        return result
    }

    private fun drawObject(x: Float, y: Float, canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawCircle(
            x,
            y,
            CIRCLE_RADIUS,
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
        const val CIRCLE_RADIUS = 50f
        const val STROKE_WIDTH = 5f
        const val MAX_FRAME_TIME = (1000 / 30)
    }
}