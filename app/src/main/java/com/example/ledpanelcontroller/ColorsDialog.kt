package com.example.ledpanelcontroller

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ledpanelcontroller.mode.ModeManager
import com.example.ledpanelcontroller.mode.Modes
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorsDialog(
    onDismissRequest : () -> Unit,
    onReturn : (Color) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier
                .size(300.dp, 350.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(5.dp)),
            horizontalAlignment = Alignment.CenterHorizontally) {

            val controller = rememberColorPickerController()
            HsvColorPicker(
                modifier = Modifier
                    .height(300.dp)
                    .padding(10.dp)
                    .background(Color.Transparent),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    onReturn.invoke(colorEnvelope.color)
                }
            )
            Button(onClick = {
                onDismissRequest.invoke()
            },
                modifier = Modifier.padding(5.dp)
            ) {
                Text("OK")
            }
        }
    }
}