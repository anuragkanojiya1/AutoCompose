package com.example.autocompose.auth

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.autocompose.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class GoogleSignInUtils {

    companion object {
        private const val TAG = "GoogleSignIn"

        // Track if sign-in is in progress to prevent multiple attempts
        private var isSignInInProgress = false

        fun doGoogleSignIn(
            context: Context,
            scope: CoroutineScope,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            login: () -> Unit
        ) {
            Log.d(TAG, "Starting Google Sign-In process")

            // Prevent multiple sign-in attempts
            if (isSignInInProgress) {
                Log.d(TAG, "Sign-in already in progress, ignoring request")
                return
            }
            isSignInInProgress = true

            val credentialManager = CredentialManager.create(context)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()
            Log.d(TAG, "Credential request built, launching coroutine")

            scope.launch {
                try {
                    Log.d(TAG, "Requesting credential from CredentialManager")
                    val startTime = System.currentTimeMillis()
                    val result = credentialManager.getCredential(context, request)
                    val credentialTime = System.currentTimeMillis() - startTime
                    Log.d(TAG, "Credential received in $credentialTime ms")

                    when (result.credential) {
                        is CustomCredential -> {
                            if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                Log.d(TAG, "Processing Google ID token credential")
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                                val googleTokenId = googleIdTokenCredential.idToken

                                Log.d(
                                    TAG,
                                    "Google ID token retrieved, length: ${googleTokenId.length}"
                                )
                                try {
                                    Log.d(TAG, "User email: ${googleIdTokenCredential.displayName}")
                                } catch (e: Exception) {
                                    Log.w(TAG, "Could not retrieve user display name")
                                }

                                val authCredential =
                                    GoogleAuthProvider.getCredential(googleTokenId, null)

                                Log.d(TAG, "Signing in with Firebase using Google credential")
                                val firebaseStartTime = System.currentTimeMillis()
                                var retryCount = 0
                                val maxRetries = 2

                                try {
                                    // Use withTimeoutOrNull to ensure Firebase auth doesn't hang indefinitely (increased for physical devices)
                                    var user = withTimeoutOrNull(15000) { // 15 seconds timeout
                                        Firebase.auth.signInWithCredential(authCredential)
                                            .await().user
                                    }
                                    val firebaseTime =
                                        System.currentTimeMillis() - firebaseStartTime
                                    Log.d(TAG, "Firebase sign-in completed in $firebaseTime ms")

                                    if (user == null) {
                                        Log.e(
                                            TAG,
                                            "Firebase sign-in returned null after ${firebaseTime}ms. Checking network..."
                                        )

                                        // If on a physical device, we might need to retry
                                        if (retryCount < maxRetries) {
                                            Log.d(
                                                TAG,
                                                "Retrying Firebase sign-in (attempt ${retryCount + 1})"
                                            )
                                            retryCount++
                                            // Small delay before retry
                                            kotlinx.coroutines.delay(1000)
                                            // Try again with the same credential
                                            val retryUser = withTimeoutOrNull(15000) {
                                                Firebase.auth.signInWithCredential(authCredential)
                                                    .await().user
                                            }
                                            if (retryUser != null) {
                                                Log.d(TAG, "Retry successful!")
                                                user = retryUser
                                            } else {
                                                Log.e(
                                                    TAG,
                                                    "Firebase sign-in timed out or returned null after ${System.currentTimeMillis() - firebaseStartTime}ms"
                                                )
                                            }
                                        }
                                    }

                                    user?.let {
                                        if (it.isAnonymous.not()) {
                                            Log.d(
                                                TAG,
                                                "Non-anonymous user authenticated, invoking login callback"
                                            )
                                            login.invoke()
                                        } else {
                                            Log.w(
                                                TAG,
                                                "User is anonymous, not invoking login callback"
                                            )
                                        }
                                    } ?: Log.w(TAG, "User is null after Firebase sign-in")
                                } catch (e: Exception) {
                                    val firebaseTime =
                                        System.currentTimeMillis() - firebaseStartTime
                                    Log.e(
                                        TAG,
                                        "Firebase sign-in failed after ${firebaseTime}ms: ${e.message}",
                                        e
                                    )

                                    // Report more specific network-related errors
                                    if (e.message?.contains("network", ignoreCase = true) == true ||
                                        e.message?.contains("timeout", ignoreCase = true) == true
                                    ) {
                                        Log.e(
                                            TAG,
                                            "Network issue detected during sign-in. Check device connectivity."
                                        )
                                    }
                                }
                            } else {
                                Log.w(
                                    TAG,
                                    "Received non-Google credential type: ${result.credential.type}"
                                )
                            }
                        }
                        else -> {
                            Log.w(TAG, "Received unknown credential type: ${result.credential}")
                        }
                    }
                } catch (e: NoCredentialException) {
                    Log.w(TAG, "No credential found, launching account picker: ${e.message}")
                    launcher?.launch(getIntent())
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "Error getting credential", e)
                    e.printStackTrace()
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error during sign-in process", e)
                    e.printStackTrace()
                } finally {
                    // Always reset the sign-in state
                    Log.d(TAG, "Resetting sign-in progress state")
                    isSignInInProgress = false
                }
            }
        }

        private fun getIntent(): Intent {
            Log.d(TAG, "Creating account picker intent")
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        private fun getCredentialOptions(context: Context): CredentialOption {
            Log.d(TAG, "Building Google ID credential options")
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        }

        fun signOut(context: Context) {
            Log.d(TAG, "Starting sign-out process")
            val auth = FirebaseAuth.getInstance()
            val credentialManager = CredentialManager.create(context)

            auth.signOut()
            Log.d(TAG, "Firebase sign-out completed")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "Clearing credential state")
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                    Log.d(TAG, "Credential state cleared successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Could not clear credentials: ${e.localizedMessage}", e)
                }
            }
        }
    }
}
