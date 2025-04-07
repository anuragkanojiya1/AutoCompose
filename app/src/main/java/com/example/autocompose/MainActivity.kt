package com.example.autocompose

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.autocompose.ui.composables.AgentScreen
import com.example.autocompose.ui.composables.DraftAgentScreen
import com.example.autocompose.ui.composables.HomeScreen
import com.example.autocompose.ui.navigation.NavGraph
import com.example.autocompose.ui.theme.AutoComposeTheme
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel
import com.example.autocompose.ui.viewmodel.FrequentEmailViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale

class MainActivity : ComponentActivity() {
    var speechInput = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Create ViewModels
            val autoComposeViewModel = AutoComposeViewmodel()
            val frequentEmailViewModel = FrequentEmailViewModel(application)
            Log.d("MainActivity", "Initializing ViewModels")

            AutoComposeTheme {
                val navController = rememberNavController()

                // Log navigation events
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    Log.d("MainActivity", "Navigated to: ${destination.route}")
                }

                NavGraph(navController, frequentEmailViewModel, autoComposeViewModel, application)
            }
        }
    }

    fun askSpeechInput(context: Context) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Speech not Available", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk")
            Log.d("MainActivity", "Launching speech recognition")
            startActivityForResult(intent, 102)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            speechInput.value = result?.get(0).toString()
            Log.d("MainActivity", "Speech recognition result: '${speechInput.value}'")
        } else if (requestCode == 102) {
            Log.d("MainActivity", "Speech recognition failed or was cancelled. Result code: $resultCode")
        }
    }
}