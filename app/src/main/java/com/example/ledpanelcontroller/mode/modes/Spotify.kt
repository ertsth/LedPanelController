package com.example.ledpanelcontroller.mode.modes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ledpanelcontroller.mode.Message
import com.example.ledpanelcontroller.mode.Modes
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL


@SuppressLint("StaticFieldLeak")
object Spotify : IMode {

    private const val mRedirectUri = "https://github.com/ertsth/rpi-rgb-led-panel"
    private const val mClientId = "9b75dcd1f6ed437fbba1458236d263c2"
    lateinit var mRequestLoginSpotify : ActivityResultLauncher<Intent>

    lateinit var mContextActivity : Activity
    lateinit var mAccessToken : String
    private lateinit var mHttpRequestThread : HttpRequestThread
    private var mLatestURI : String = ""

    private val mPermissions = arrayOf(
        "streaming",
        "user-read-currently-playing"
    )

    @Composable
    override fun LedModeSettings(modifier: Modifier) {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Enjoy!")
        }
    }

    override fun onModeActivated() {
        authForToken()
    }

    override fun onModeDeactivated() {
        mHttpRequestThread.join()
    }


    private fun authForToken() {
        val request = AuthorizationRequest.Builder(mClientId,
            AuthorizationResponse.Type.TOKEN, mRedirectUri)
            .setScopes(mPermissions)
            .build()
        val intent = AuthorizationClient.createLoginActivityIntent(mContextActivity, request)
        mRequestLoginSpotify.launch(intent)
    }

    fun processResponse(resultCode : Int, data : Intent?) {
        val response = AuthorizationClient.getResponse(resultCode, data)

        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                mAccessToken = response.accessToken
                mHttpRequestThread = HttpRequestThread()
                mHttpRequestThread.start()
            }
            AuthorizationResponse.Type.ERROR -> {
                // TODO
            }
            else -> {
                // TODO
            }
        }
    }

    private fun sendToken() {
        Message()
            .addModeName(Modes.SPOTIFY.name)
            .addParameter("token", mAccessToken)
            .send()
    }

    private class HttpRequestThread() : Thread() {
        override fun run() {
            while (true) {
                httpRequest()
                Thread.sleep(1000)
            }
        }
    }

    private fun httpRequest() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/player/currently-playing")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DEBUG","Failed to fetch data: $e")
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonObject = JSONObject(response.body!!.string())
//                    Log.d("DEBUG",jsonObject.toString(3))
                    processJSON(jsonObject)
                } catch (e: JSONException) {
                    Log.d("DEBUG","Failed to parse data: $e")
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun processJSON(json : JSONObject) {
        val itemJSON = json.getJSONObject("item")
        val albumJSON = itemJSON.getJSONObject("album")
        val imagesArray = albumJSON.getJSONArray("images")
        val imageJSON = imagesArray.getJSONObject(0)
        val imageUrl = imageJSON.getString("url")
        Log.d("DEBUG","Image url: $imageUrl")
        if (mLatestURI != imageUrl) {
            mLatestURI = imageUrl
            val url = URL(imageUrl)
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            Image.processBitmap(bmp)
        }
    }
}