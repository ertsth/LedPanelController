package com.example.ledpanelcontroller.mode.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

object TestMode : IMode {
    @Composable
    override fun LedModeSettings(modifier : Modifier) {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Choose a mode to start!", modifier)
        }
    }
}