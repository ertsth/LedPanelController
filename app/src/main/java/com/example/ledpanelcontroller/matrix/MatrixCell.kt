package com.example.ledpanelcontroller.matrix

import androidx.compose.ui.graphics.Color

data class MatrixCell(
    var color : Color = Color.Black,

) {
    override fun toString(): String {
        return " r:${color.red}, g:${color.green}, b:${color.blue}"
    }
}