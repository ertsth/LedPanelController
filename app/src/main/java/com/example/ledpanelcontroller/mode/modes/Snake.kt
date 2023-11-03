package com.example.ledpanelcontroller.mode.modes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ledpanelcontroller.ColorsDialog
import com.example.ledpanelcontroller.mode.Message
import com.example.ledpanelcontroller.mode.Modes
import kotlin.math.roundToInt

object Snake : IMode {

    @Composable
    override fun LedModeSettings(modifier: Modifier) {
        val scrollState = rememberScrollState()
        Column(modifier = modifier
            .padding(10.dp)
            .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoveButton(onClick = {sendNewDirection(Direction.UP)}) {
                Icon(Icons.Default.KeyboardArrowUp, "Up")
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                MoveButton(onClick = {sendNewDirection(Direction.LEFT)},
                    modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Left")
                }
                MoveButton(onClick = {sendNewDirection(Direction.RIGHT)},
                    modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.KeyboardArrowRight, "Right")
                }
            }
            MoveButton(onClick = {sendNewDirection(Direction.DOWN)}) {
                Icon(Icons.Default.KeyboardArrowDown, "Down")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                val chooseColorSnake = remember { mutableStateOf(false)}
                val chooseColorFood = remember { mutableStateOf(false)}
                Button(onClick = {
                    chooseColorSnake.value = true
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Color for Snake")
                    if(chooseColorSnake.value) {
                        ColorsDialog({
                            chooseColorSnake.value = false
                        }
                        ) {
                            sendNewColorSnake(it)
                        }
                    }
                }
                Button(onClick = {
                    chooseColorFood.value = true
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Color for Food")
                    if (chooseColorFood.value) {
                        ColorsDialog({
                            chooseColorFood.value = false
                        }
                        ) {
                            sendNewColorFood(it)
                        }
                    }
                }
            }
            val sliderPosition = remember { mutableStateOf(1f) }
            Text(text = "Timeout: x${(sliderPosition.value * 100.0).roundToInt() / 100.0}")
            Slider(
                steps = 11,
                value = sliderPosition.value,
                onValueChange = {
                    sliderPosition.value = it
                    sendNewTimeout(sliderPosition.value) },
                valueRange = 0f..2f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(10.dp)
            )
        }
    }

    @Composable
    private fun MoveButton(modifier : Modifier = Modifier,
                           onClick : () -> Unit,
                           content : @Composable () -> Unit) {
        IconButton(modifier = Modifier
            .size(80.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, shape = CircleShape),
            onClick = onClick
        ) {
            content.invoke()
        }
    }

    private fun sendNewDirection(direction : Snake.Direction) {
        Message()
            .addModeName(Modes.SNAKE.name)
            .addParameter("direction", direction.value)
            .send()
    }

    private fun sendNewColorSnake(color : Color) {
        Message()
            .addModeName(Modes.SNAKE.name)
            .addParameter("color_snake", Message.addColor(color))
            .send()
    }

    private fun sendNewColorFood(color : Color) {
        Message()
            .addModeName(Modes.SNAKE.name)
            .addParameter("color_food", Message.addColor(color))
            .send()
    }

    private fun sendNewTimeout(timeout : Float) {
        Message()
            .addModeName(Modes.SNAKE.name)
            .addParameter("timeout", String.format("%.2f", timeout))
            .send()
    }

    enum class Direction (val value : String) {
        UP("up"),
        DOWN("down"),
        RIGHT("right"),
        LEFT("left")
    }
}