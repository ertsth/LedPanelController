package com.example.ledpanelcontroller.mode.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object DummyMode : IMode {
    @Composable
    override fun LedModeSettings(modifier : Modifier) {
        Column(modifier = modifier) {
            Text("I'm a dummy!", modifier)
        }
    }
}