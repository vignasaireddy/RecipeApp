package uk.ac.tees.mad.recipeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
}