package com.example.ledpanelcontroller.mode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun ModesDialog(
    onDismissRequest : () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            Modifier
                .size(300.dp, 300.dp)
                .background(MaterialTheme.colorScheme.background,
                    RoundedCornerShape(5.dp))) {
            val data = Modes.values().filter { it.name != "TEST" }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(data) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Card(modifier = Modifier
                            .padding(5.dp)
                            .size(90.dp)
                            .clickable {
                                ModeManager.setActiveMode(it)
                                onDismissRequest.invoke()
                            }) {
                            Image(painter = painterResource(id = it.imageSource),
                                contentDescription = "Mode logo",
                                contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize())
                        }
                        Text(
                            text = "  ${it.name}",
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}