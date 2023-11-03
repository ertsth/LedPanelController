package com.example.ledpanelcontroller

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.ledpanelcontroller.bluetooth.BluetoothHandler
import com.example.ledpanelcontroller.matrix.MatrixUI
import com.example.ledpanelcontroller.mode.ModeManager
import com.example.ledpanelcontroller.mode.ModesDialog
import com.example.ledpanelcontroller.mode.modes.Image
import com.example.ledpanelcontroller.mode.modes.Spotify
import com.example.ledpanelcontroller.ui.theme.LedPanelControllerTheme
import kotlin.concurrent.thread


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LedPanelControllerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background) {
                    MainColumn(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        registerIntentLaunchers()
        Spotify.mContextActivity = this
    }

    override fun onDestroy() {
        BluetoothHandler.disconnect()
        super.onDestroy()
    }

    lateinit var mRequestEnableBluetooth : ActivityResultLauncher<Intent>
    lateinit var mRequestBluetoothConnectPermission : ActivityResultLauncher<String>
    lateinit var mCropImageLauncher :  ActivityResultLauncher<CropImageContractOptions>

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun registerIntentLaunchers() {
        mRequestEnableBluetooth = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result: ActivityResult ->
            when(result.resultCode) {
                Activity.RESULT_OK -> {
                    Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_LONG).show()
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_LONG).show()
                }
            }
        }

        mRequestBluetoothConnectPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                connectToMatrix()
            } else {
                Toast.makeText(this, "Permission is not granted, can't proceed further", Toast.LENGTH_LONG).show()
            }
        }

        mCropImageLauncher = registerForActivityResult(CropImageContract()) {
                result ->
            if (result.isSuccessful) {
                val uri = result.uriContent
                val input = this.contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(input)
                Image.processBitmap(bitmap)
                input!!.close()
            } else {
                val exception = result.error
            }
        }

        Spotify.mRequestLoginSpotify = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
                result: ActivityResult ->
            Spotify.processResponse(result.resultCode, result.data)
        }

        Image.mPhotoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            mCropImageLauncher.launch(
                CropImageContractOptions(uri,
                    CropImageOptions(
                        guidelines = CropImageView.Guidelines.ON,
                        cropShape = CropImageView.CropShape.RECTANGLE,
                        fixAspectRatio = true)
                )
            )
        }
    }

    @Composable
    fun MainColumn(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            SettingsRow(Modifier.fillMaxWidth())
            MatrixView(Modifier.fillMaxWidth())
            LedModeSettings(Modifier.fillMaxSize())
        }
    }

    @Composable
    fun SettingsRow(modifier: Modifier = Modifier) {
        val showDialog = remember { mutableStateOf(false)}
        Row(horizontalArrangement = Arrangement.End,
            modifier = modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(5.dp)
        ) {
            val buttonModifier = Modifier.size(50.dp)
            OutlinedButton(modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    showDialog.value = !showDialog.value
                }
            ) {
                Image(painter = painterResource(id = R.drawable.modes),
                    contentDescription = "Modes", contentScale = ContentScale.FillBounds)
            }
            if (showDialog.value) {
                ModesDialog {
                    showDialog.value = false
                }
            }

            OutlinedButton(modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    if (!BluetoothHandler.isMatrixConnected()) {
                        initiateBluetoothConnection()
                    }
                }
            ) {
                Image(painter = painterResource(id = R.drawable.bluetooth),
                    contentDescription = "Bluetooth", contentScale = ContentScale.FillBounds)
            }

        }
    }

    private lateinit var mBtAdapter: BluetoothAdapter

    private fun initiateBluetoothConnection() {
        val btManager = ContextCompat.getSystemService(this, BluetoothManager::class.java)
        if (btManager == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show()
            return
        }

        val btAdapter = btManager.adapter
        if (btAdapter == null) {
            Toast.makeText(this, "Cannot start Bluetooth", Toast.LENGTH_LONG).show()
            return
        }

        if (!btAdapter.isEnabled) {
            mRequestEnableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED -> {
                mBtAdapter = btAdapter
                connectToMatrix()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) -> {
                mBtAdapter = btAdapter
                Toast.makeText(this, "Bluetooth permission is required!", Toast.LENGTH_LONG).show()
                mRequestBluetoothConnectPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
            else -> {
                mBtAdapter = btAdapter
                mRequestBluetoothConnectPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

    private fun connectToMatrix() {
        val status = BluetoothHandler.connect(mBtAdapter)
        if (status) {
            Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error, Matrix is not connected!", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun MatrixView(modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .background(MaterialTheme.colorScheme.outline)
        .padding(60.dp)
        .size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        MatrixUI.Matrix(modifier);
    }
}

@Composable
fun LedModeSettings(modifier: Modifier = Modifier) {
    ModeManager.getActiveMode().LedModeSettings(modifier)
}