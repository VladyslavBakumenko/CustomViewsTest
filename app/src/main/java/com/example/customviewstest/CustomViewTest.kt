package com.example.customviewstest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates


typealias CustomViewActionTestListener = (row: Int, column: Int) -> Unit

class CustomViewTest(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var field = CustomFieldsTest(TEST_ROWS, TEST_COLUMNS)
        set(value) {
            field.listeners.remove(listener)
            field = value
            value.listeners.add(listener)
            updateViewSizes()
            requestLayout()
            invalidate()
        }
    var actionListener: CustomViewActionTestListener? = null

    private var player1Color by Delegates.notNull<Int>()
    private var player2Color by Delegates.notNull<Int>()
    private var gridColor by Delegates.notNull<Int>()

    private val fieldRect = RectF(0f, 0f, 0f, 0f)
    private var cellSize: Float = 0f
    private var cellPadding: Float = 0f

    private lateinit var player1Paint: Paint
    private lateinit var player2Paint: Paint
    private lateinit var gridPaint: Paint

    private val cellRect = RectF()

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        R.style.DefaultCustomViewTestStyle
    )

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.customViewTestFieldStyle
    )

    constructor(context: Context) : this(context, null)

    init {
        if (attrs != null) {
            initAttrs(attrs, defStyleAttr, defStyleRes)
        } else {
            initDefColors()
        }
        initPaints()

    }

    private fun initPaints() {

        player1Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        player1Paint.color = player1Color
        player1Paint.style = Paint.Style.STROKE
        player1Paint.strokeWidth = TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)

        player2Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        player2Paint.color = Color.BLUE
        player2Paint.style = Paint.Style.STROKE
        player2Paint.strokeWidth = TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)

        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = Color.RED
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var result = false
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                result = true
            }
            MotionEvent.ACTION_UP -> {
                val row = getRow(event)
                val column = getRow(event)
                actionListener?.invoke(row, column)
                result = true
            }
        }
        return result
    }

    private fun getRow(event: MotionEvent): Int {
        return ((event.y - fieldRect.top) / cellSize).toInt()
    }

    private fun getColumn(event: MotionEvent): Int {
        Log.d("fdfdfdfdf", fieldRect.right.toString())
        Log.d("fdfdfdfdf", "${fieldRect.left / cellSize}")

        return ((event.x - fieldRect.right) / cellSize).toInt()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        field.listeners.add(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        field.listeners.remove(listener)
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomViewTest,
            defStyleAttr,
            defStyleRes
        )
        player1Color = typedArray.getColor(R.styleable.CustomViewTest_player1Color, Color.GRAY)
        player2Color = typedArray.getColor(R.styleable.CustomViewTest_player2Color, Color.RED)
        gridColor = typedArray.getColor(R.styleable.CustomViewTest_gridColor, Color.BLACK)
        typedArray.recycle()
    }

    private fun initDefColors() {
        player1Color = Color.GRAY
        player2Color = Color.RED
        gridColor = Color.BLACK
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWith = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DESIRED_CELL_SIZE,
            resources.displayMetrics
        ).toInt()

        val rows = field.rows
        val columns = field.columns

        val desiredWith =
            max(minWith, columns * desiredCellSizeInPixels + paddingRight + paddingRight)
        val desiredHeight =
            max(minHeight, rows * desiredCellSizeInPixels + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWith, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSizes()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (cellSize == 0f) return
        drawGrid(canvas)
        drawCells(canvas)
    }

    private fun drawGrid(canvas: Canvas?) {
        val field = this.field
        val xStart = fieldRect.left
        val xEnd = fieldRect.right

        for (i in 0..field.rows) {
            val y = fieldRect.top + cellSize * i
            canvas?.drawLine(xStart, y, xEnd, y, gridPaint)
        }

        val yStart = fieldRect.top
        val yEnd = fieldRect.bottom
        for (i in 0..field.columns) {
            val x = fieldRect.left + cellSize * i
            canvas?.drawLine(x, yStart, x, yEnd, gridPaint)
        }
    }

    private fun drawCells(canvas: Canvas?) {
        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                val cell = field.getCell(row, column)
                if (cell == Cell.PLAYER_1) {
                    drawPlayer1(canvas, row, column)
                } else if (cell == Cell.PLAYER_2) {
                    drawPlayer2(canvas, row, column)
                }
            }
        }
    }

    private fun drawPlayer1(canvas: Canvas?, row: Int, column: Int) {
        val cellRect = getCellRect(row, column)
        canvas?.drawLine(cellRect.left, cellRect.top, cellRect.right, cellRect.bottom, player1Paint)
        canvas?.drawLine(cellRect.right, cellRect.top, cellRect.left, cellRect.bottom, player1Paint)
    }

    private fun drawPlayer2(canvas: Canvas?, row: Int, column: Int) {
        val cellRect = getCellRect(row, column)

        canvas?.drawCircle(
            cellRect.centerX(),
            cellRect.centerY(),
            cellRect.width() / 2,
            player2Paint
        )
    }


    private fun getCellRect(row: Int, column: Int): RectF {
        cellRect.left = fieldRect.left + column * cellSize + cellPadding
        cellRect.top = fieldRect.top + row * cellSize + cellPadding
        cellRect.right = cellRect.left + cellSize - cellPadding * 2
        cellRect.bottom = cellRect.top + cellSize - cellPadding * 2
        return cellRect
    }

    private fun updateViewSizes() {
        val field = this.field

        val safeWith = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWith = safeWith / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWith, cellHeight)
        cellPadding = cellSize * 0.2f

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWith - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    private val listener: OnFieldChangerListener = {
        invalidate()
    }

    companion object {
        const val TEST_ROWS = 9
        const val TEST_COLUMNS = 5

        const val DESIRED_CELL_SIZE = 50f
    }
}
