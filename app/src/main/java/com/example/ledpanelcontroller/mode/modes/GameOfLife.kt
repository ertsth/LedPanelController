package com.example.ledpanelcontroller.mode.modes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ledpanelcontroller.ColorsDialog
import com.example.ledpanelcontroller.mode.Message
import com.example.ledpanelcontroller.mode.Modes
import kotlin.math.roundToInt

object GameOfLife : IMode {
    private var mRuleOfBirth : String = "3"
    private var mRuleOfSurvive : String = "23"

    private var mColorAlive : Color = Color.Green
    private var mColorDead : Color = Color.Black

    private var mAction = Actions.STOP

    private var mTimeout = 1f

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun LedModeSettings(modifier: Modifier) {
        Column(modifier = modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                val ruleOfBirth = remember { mutableStateOf("3") }
                val ruleOfSurvive = remember { mutableStateOf("23") }
                TextField(value = ruleOfBirth.value,
                    onValueChange = { new ->
                        ruleOfBirth.value = new
                        mRuleOfBirth = new
                        sendNewRuleBirth()
                    },
                    singleLine = true,
                    label = { Text(text = "Rule of Birth") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                )
                TextField(value = ruleOfSurvive.value,
                    onValueChange = { new ->
                        ruleOfSurvive.value = new
                        mRuleOfSurvive = new
                        sendNewRuleSurvive()
                    },
                    singleLine = true,
                    label = { Text(text = "Rule of Survive") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                val chooseColorAlive = remember { mutableStateOf(false)}
                val chooseColorDead = remember { mutableStateOf(false)}
                Button(onClick = {
                    chooseColorAlive.value = true
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Color for alive")
                    if(chooseColorAlive.value) {
                        ColorsDialog({
                            chooseColorAlive.value = false
                        }
                        ) {
                            mColorAlive = it
                            sendNewColorAlive()
                        }
                    }
                }
                Button(onClick = {
                    chooseColorDead.value = true
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Color for dead")
                    if(chooseColorDead.value) {
                        ColorsDialog({
                            chooseColorDead.value = false
                        }
                        ) {
                            mColorDead = it
                            sendNewColorDead()
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    mAction = Actions.START
                    sendNewAction()
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start")
                }
                Button(onClick = {
                    mAction = Actions.STOP
                    sendNewAction()
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Stop")
                }
                Button(onClick = {
                    mAction = Actions.STEP
                    sendNewAction()
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Step")
                }
            }
            val sliderPosition = remember { mutableStateOf(1f) }
            Text(text = "Timeout: x${(sliderPosition.value * 100.0).roundToInt() / 100.0}")
            Slider(
                steps = 11,
                value = sliderPosition.value,
                onValueChange = {
                    sliderPosition.value = it
                    mTimeout = sliderPosition.value
                    sendNewTimeout() },
                valueRange = 0f..2f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(10.dp)
            )
        }
    }

    private fun sendNewAction() {
        Message()
            .addModeName(Modes.GAME_OF_LIFE.name)
            .addParameter("action", mAction.value)
            .send()
    }

    private fun sendNewColorAlive() {
        Message()
            .addModeName(Modes.GAME_OF_LIFE.name)
            .addParameter("color_alive", Message.addColor(mColorAlive))
            .send()
    }

    private fun sendNewColorDead() {
        Message()
            .addModeName(Modes.GAME_OF_LIFE.name)
            .addParameter("color_dead", Message.addColor(mColorDead))
            .send()
    }

    private fun sendNewRuleBirth() {
        Message()
            .addModeName(Modes.GAME_OF_LIFE.name)
            .addParameter("rule_birth", mRuleOfBirth)
            .send()
    }

    private fun sendNewRuleSurvive() {
        Message()
            .addModeName(Modes.GAME_OF_LIFE.name)
            .addParameter("rule_survive", mRuleOfSurvive)
            .send()
    }

    private fun sendNewTimeout() {
        Message()
            .addModeName(Modes.GAME_OF_LIFE.name)
            .addParameter("timeout", String.format("%.2f", mTimeout))
            .send()
    }

    enum class Actions (val value : String) {
        START("start"),
        STOP("stop"),
        STEP("step")
    }
}
