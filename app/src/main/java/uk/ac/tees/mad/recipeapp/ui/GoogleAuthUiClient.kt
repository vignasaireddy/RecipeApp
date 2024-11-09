package uk.ac.tees.mad.recipeapp.ui

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.recipeapp.data.SignInResult
import uk.ac.tees.mad.recipeapp.data.UserData
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthUiClient(
    private val signInClient: SignInClient
) {
    private val firebaseAuth = Firebase.auth
    val firestore = Firebase.firestore

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
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user
            val userData = user?.let {
                UserData(
                    userId = it.uid,
                    username = it.displayName,
                    email = it.email
                )
            }

            if (userData != null) {
                saveUserDataToFirestore(userData)
            }

            SignInResult(
                data = userData,
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

    private suspend fun saveUserDataToFirestore(userData: UserData) {
        try {
            val documentSnapshot = firestore.collection("users").document(userData.userId).get().await()
            if (!documentSnapshot.exists()) {
                firestore.collection("users")
                    .document(userData.userId)
                    .set(userData)
                    .await()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            if (exception is CancellationException) throw exception
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
                    .setServerClientId("306161792065-pceahnog34ksuh6eep82sr22fanhhi1p.apps.googleusercontent.com")
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
