package com.example.ledpanelcontroller.mode.modes

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ledpanelcontroller.matrix.Matrix
import com.example.ledpanelcontroller.mode.Message
import com.example.ledpanelcontroller.mode.Modes
import kotlin.concurrent.thread

object Image : IMode {
    lateinit var mPhotoPicker : ActivityResultLauncher<PickVisualMediaRequest>
    private val mImage = mutableStateOf(Uri.EMPTY)

    @Composable
    override fun LedModeSettings(modifier: Modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
            Button(onClick = {
                mPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)) {
                Text("Choose a photo...")
            }
            AsyncImage(model = mImage.value, contentDescription = "Your Image",
                contentScale = ContentScale.FillBounds, alignment = Alignment.Center,
                modifier = Modifier.size(200.dp))
        }
    }

    override fun onModeDeactivated() {
        Matrix.clear()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun processBitmap(bitmap : Bitmap) {
        thread {
            val stepx = bitmap.width / 64
            val stepy = bitmap.height / 64
            for (i in 0 until 64) {
                for (j in 0 until 64) {
                    val color = bitmap.getColor(i * stepx, j * stepy)
                    processPixel(i, j, Color(color.red(), color.green(), color.blue()))
                }
            }
        }
    }

    private fun processPixel(x : Int, y : Int, color : Color) {
        Matrix.setPixel(x, y, color)

        Message()
            .addModeName(Modes.IMAGE.name)
            .setPixel(x, y, color)
            .send()
    }

}