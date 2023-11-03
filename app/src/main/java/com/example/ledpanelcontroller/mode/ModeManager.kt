package com.example.ledpanelcontroller.mode

import androidx.compose.runtime.mutableStateOf
import com.example.ledpanelcontroller.mode.modes.Custom
import com.example.ledpanelcontroller.mode.modes.GameOfLife
import com.example.ledpanelcontroller.mode.modes.IMode
import com.example.ledpanelcontroller.mode.modes.Image
import com.example.ledpanelcontroller.mode.modes.Paint
import com.example.ledpanelcontroller.mode.modes.Snake
import com.example.ledpanelcontroller.mode.modes.Spotify
import com.example.ledpanelcontroller.mode.modes.TestMode

object ModeManager {
    private var activeMode = mutableStateOf(Modes.TEST)

    fun getActiveMode() : IMode {
        return when(activeMode.value) {
            Modes.PAINT -> Paint
            Modes.SPOTIFY -> Spotify
            Modes.TEST -> TestMode
            Modes.GAME_OF_LIFE -> GameOfLife
            Modes.IMAGE -> Image
            Modes.SNAKE -> Snake
            Modes.CUSTOM -> Custom
        }
    }

    fun setActiveMode(mode : Modes) {
        getActiveMode().onModeDeactivated()
        activeMode.value = mode
        Message().modeChanged(mode).send();
        getActiveMode().onModeActivated()
    }
}