package com.example.ledpanelcontroller.mode

import com.example.ledpanelcontroller.R

enum class Modes(val imageSource : Int = R.drawable.ic_launcher_foreground) {
    PAINT(imageSource = R.drawable.paint),
    SPOTIFY(imageSource = R.drawable.spotify),
    GAME_OF_LIFE(imageSource = R.drawable.gameoflife),
    IMAGE(imageSource = R.drawable.image),
    SNAKE(imageSource = R.drawable.snake),
    CUSTOM(imageSource = R.drawable.custom),
    TEST;
}