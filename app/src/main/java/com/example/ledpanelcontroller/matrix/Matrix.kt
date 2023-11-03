package com.example.ledpanelcontroller.matrix

import androidx.compose.ui.graphics.Color

object Matrix {
    private const val mWidth = 64
    private const val mHeight = 64

    private var matrix : Array<Array<MatrixCell>> = Array(0){Array(0) { MatrixCell() } }
    private val DEFAULT_COLOR : Color = Color.Black

    init {
        matrix = Array(mWidth){Array(mHeight) { MatrixCell() } }
    }

    fun getWidth() : Int {
        return matrix.size
    }

    fun getHeight() : Int {
        return matrix[0].size
    }

    fun getState() : Array<Array<MatrixCell>> {
        return matrix
    }

    fun setPixel(w : Int, h : Int, color : Color) {
        matrix[w][h].color = color
        MatrixUI.update()
    }

    fun fill(color: Color) {
        matrix.forEach { arr -> arr.forEach{pxl -> pxl.color = color } }
        MatrixUI.update()
    }

    fun clear() {
        matrix.forEach { arr -> arr.forEach{ pxl -> pxl.color = DEFAULT_COLOR } }
        MatrixUI.update()
    }
}