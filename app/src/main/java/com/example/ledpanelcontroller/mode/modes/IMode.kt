package com.example.ledpanelcontroller.mode.modes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface IMode {
    @Composable
    fun LedModeSettings(modifier : Modifier)
    fun onModeActivated() {}
    fun onModeDeactivated() {}
}