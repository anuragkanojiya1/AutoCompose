package com.example.autocompose

import android.app.Activity
import android.content.ContentValues.TAG
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.autocompose.ui.navigation.NavGraph
import com.example.autocompose.ui.theme.AutoComposeTheme
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel
import com.example.autocompose.ui.viewmodel.FrequentEmailViewModel
import com.example.autocompose.ui.viewmodel.PaymentViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    var speechInput = mutableStateOf("")
//    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        paymentViewModel = PaymentViewModel()
        auth = Firebase.auth
        Log.d("MainActivity", "Initialized shared PaymentViewModel")

        setContent {
            val autoComposeViewModel = AutoComposeViewmodel()
            val frequentEmailViewModel = FrequentEmailViewModel(application)
            Log.d("MainActivity", "Initializing ViewModels")

            AutoComposeTheme {
                val navController = rememberNavController()

                // Log navigation events
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    Log.d("MainActivity", "Navigated to: ${destination.route}")
                }

                NavGraph(
                    navController = navController,
                    frequentEmailViewModel = frequentEmailViewModel,
                    autoComposeViewmodel = autoComposeViewModel,
                    paymentViewModel = paymentViewModel,
                    application = application,
                )
            }
        }
    }

    // Handle payment redirects from browser
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d("MainActivity", "Received new intent: ${intent.data}")

        intent.data?.let { uri ->
            if (uri.scheme == "com.example.autocompose" && uri.host == "paypalpay") {
                Log.d("MainActivity", "Processing PayPal redirect: $uri")

                val token = uri.getQueryParameter("token")
                val payerId = uri.getQueryParameter("PayerID")
                val opType = uri.getQueryParameter("opType")

                Log.d(
                    "MainActivity",
                    "Payment parameters: token=$token, PayerID=$payerId, opType=$opType"
                )

                if (token != null && payerId != null) {
                    Log.d(
                        "MainActivity",
                        "Captured successful payment approval, capturing order with token: $token"
                    )
                    paymentViewModel.captureOrder(token)
                } else if (opType == "cancel") {
                    Log.d("MainActivity", "User cancelled payment")
                    paymentViewModel.handlePaymentCancellation()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        updateUI(currentUser)
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
