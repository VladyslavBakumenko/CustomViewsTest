package com.example.customviewstest

enum class Cell {
    PLAYER_1,
    PLAYER_2,
    EMPTY
}

typealias OnFieldChangerListener = (field: CustomFieldsTest) -> Unit

class CustomFieldsTest(val rows: Int, val columns: Int) {
    private val cells = Array(rows) { Array(columns) { Cell.EMPTY } }
    val listeners = mutableSetOf<OnFieldChangerListener>()

    fun getCell(row: Int, column: Int): Cell {
        if (row < 0 || column < 0 || row >= rows || column >= columns) return Cell.EMPTY
        return cells[row][column]
    }

    fun setCell(row: Int, column: Int, cell: Cell) {
        if (row < 0 || column < 0 || row >= rows || column >= columns) return
        if (cells[row][column] != cell) {
            cells[row][column] = cell
            listeners.forEach { it.invoke(this) }
        }
    }
}