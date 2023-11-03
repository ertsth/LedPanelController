package com.example.ledpanelcontroller.matrix

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.ledpanelcontroller.mode.modes.Paint

object MatrixUI {
    private val mMatrixWidth : Int = 64
    private val mMatrixHeight : Int = 64

    private val mFrames = mutableStateOf(0)
    private val mMatrixUiEventsSubscribers = HashSet<IMatrixUISubscriber>();

    fun subscribeOnEvents(subscriber : IMatrixUISubscriber) {
        mMatrixUiEventsSubscribers.add(subscriber);
    }

    fun unsubscribeFromEvents(subscriber : IMatrixUISubscriber) {
        mMatrixUiEventsSubscribers.remove(subscriber);
    }

    @Composable
    fun Matrix(modifier: Modifier = Modifier) {
        Canvas(modifier = modifier
            .size(300.dp)
            .background(Color.Black)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val pe = awaitPointerEvent()
                        when (pe.type) {
                            PointerEventType.Move -> {
                                val offset = pe.calculateCentroid()
                                processTouch(offset, size)
                            }

                            PointerEventType.Press -> {
                                val offset = pe.changes[0].position
                                if (offset != Offset.Unspecified) {
                                    processTouch(offset, size)
                                }
                            }
                        }
                    }
                }
            }
        ) {

            val cellSize = Size((size.width / mMatrixWidth), (size.height / mMatrixHeight))

            mFrames.value.let {
                kotlin.run {
                    val state = Matrix.getState()
                    for (i in 0 until mMatrixWidth) {
                        for (j in 0 until mMatrixHeight) {
                            val pxl = state[i][j]
                            val x = (i * (size.width / mMatrixWidth))
                            val y = (j * (size.height / mMatrixHeight))
                            drawRect(
                                color = pxl.color,
                                topLeft = Offset(x.toFloat(), y.toFloat()),
                                size = cellSize
                            )
                        }
                    }
                }
            }
        }
    }

    fun update() {
        mFrames.value++
    }

    private fun processTouch(offset : Offset, size : IntSize) {
        val w = Matrix.getWidth()
        val h = Matrix.getHeight()
        val x = ((offset.x / size.width) * w).toInt()
        val y = ((offset.y / size.height) * h).toInt()
        if (x !in 0 until w || y !in 0 until h) return

        mMatrixUiEventsSubscribers.forEach { it.onTouch(x, y) }
    }
}