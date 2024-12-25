package uk.ac.tees.mad.ra.ui

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.ra.data.SignInResult
import uk.ac.tees.mad.ra.data.UserData
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
                saveUserDataToFirestore(userData, exception = {
                    SignInResult(
                        data = null,
                        errorMessage = it.message
                    )
                })
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

    private suspend fun saveUserDataToFirestore(
        userData: UserData,
        exception: (Exception) -> Unit
    ) {
        try {
            val documentSnapshot =
                firestore.collection("users").document(userData.userId).get().await()
            if (!documentSnapshot.exists()) {
                firestore.collection("users")
                    .document(userData.userId)
                    .set(userData.toMap())
                    .await()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            if (exception is CancellationException) exception(exception)
        }
    }

    suspend fun getCurrentUser(): UserData? {
        return try {
            val result = firestore.collection("users")
                .document(firebaseAuth.currentUser?.uid ?: "")
                .get().await()
            UserData(
                userId = result.getString("userId") ?: "",
                username = result.getString("username") ?: "",
                email = result.getString("email") ?: "",
                imageUrl = result.getString("imageUrl") ?: ""
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            if (exception is CancellationException) throw exception
            null
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
                    .setServerClientId("810710003391-qcq1o9abf030ukbre3q3m48e7nv7u2lb.apps.googleusercontent.com")
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
