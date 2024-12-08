package uk.ac.tees.mad.recipeapp.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.recipeapp.data.UserData
import uk.ac.tees.mad.recipeapp.ui.GoogleAuthUiClient

class ProfileViewModel : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    fun getUserData(googleAuthUiClient: GoogleAuthUiClient) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val user = googleAuthUiClient.getCurrentUser()
                _userData.value = user
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateUserProfile(name: String, imageUri: Uri?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val updatedUser = _userData.value?.copy(username = name) ?: return@launch


                imageUri?.let { uri ->
                    val imageUrl = Firebase.storage.reference.child("profile_images")
                        .child(updatedUser.userId)
                        .putFile(uri)
                        .await()
                        .storage.downloadUrl.await().toString()

                    updatedUser.imageUrl = imageUrl
                    Firebase.firestore.collection("users")
                        .document(updatedUser.userId)
                        .set(updatedUser)
                        .await()
                }

                _userData.value = updatedUser
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }
}