package uk.ac.tees.mad.recipeapp.ui

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.recipeapp.data.SignInResult
import uk.ac.tees.mad.recipeapp.data.UserData
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthUiClient(
    private val signInClient: SignInClient
) {
    private val firebaseAuth = Firebase.auth

    suspend fun initiateSignIn(): IntentSender? {
        return try {
            val signInRequest = signInClient.beginSignIn(createSignInRequest()).await()
            signInRequest.pendingIntent.intentSender
        } catch (exception: Exception) {
            exception.printStackTrace()
            if (exception is CancellationException) throw exception
            null
        }
    }

    suspend fun handleSignInResult(intent: Intent): SignInResult {
        val credential = signInClient.getSignInCredentialFromIntent(intent)
        val idToken = credential.googleIdToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

        return try {
            val user = firebaseAuth.signInWithCredential(firebaseCredential).await().user
            SignInResult(
                data = user?.let {
                    UserData(
                        userId = it.uid,
                        username = it.displayName,
                        email = it.email
                    )
                },
                errorMessage = null
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            if (exception is CancellationException) throw exception
            SignInResult(
                data = null,
                errorMessage = exception.message
            )
        }
    }

    fun getCurrentUser(): UserData? {
        return firebaseAuth.currentUser?.let {
            UserData(
                userId = it.uid,
                username = it.displayName,
                email = it.email
            )
        }
    }

    suspend fun signOutUser() {
        try {
            signInClient.signOut().await()
            firebaseAuth.signOut()
        } catch (exception: Exception) {
            exception.printStackTrace()
            if (exception is CancellationException) throw exception
        }
    }

    private fun createSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(ServerClient)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
