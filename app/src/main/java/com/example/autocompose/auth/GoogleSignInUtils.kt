package com.example.autocompose.auth

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GoogleSignInUtils {

    companion object {

        private const val TAG = "GoogleSignIn"

        suspend fun signIn(context: Context): Result<Unit> {

            return try {

                val credentialManager = CredentialManager.create(context)

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(
                        GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setAutoSelectEnabled(false)
                            .setServerClientId(context.getString(R.string.web_client_id))
                            .build()
                    )
                    .build()

                val result = credentialManager.getCredential(context, request)

                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {

                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val googleTokenId = googleIdTokenCredential.idToken

                    val firebaseCredential =
                        GoogleAuthProvider.getCredential(googleTokenId, null)

                    Firebase.auth
                        .signInWithCredential(firebaseCredential)
                        .await()

                    Log.d(TAG, "Firebase Google sign-in success")

                    Result.success(Unit)

                } else {
                    Result.failure(Exception("Invalid credential type"))
                }

            } catch (e: NoCredentialException) {
                Log.w(TAG, "No saved account")
                Result.failure(e)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Credential error", e)
                Result.failure(e)
            } catch (e: Exception) {
                Log.e(TAG, "Google sign-in failed", e)
                Result.failure(e)
            }
        }

        suspend fun signOut(context: Context): Result<Unit> {
            return try {

                FirebaseAuth.getInstance().signOut()

                val credentialManager = CredentialManager.create(context)

                withContext(Dispatchers.IO) {
                    credentialManager.clearCredentialState(
                        ClearCredentialStateRequest()
                    )
                }

                Log.d(TAG, "Sign-out successful")

                Result.success(Unit)

            } catch (e: Exception) {
                Log.e(TAG, "Sign-out failed", e)
                Result.failure(e)
            }
        }
    }
}
