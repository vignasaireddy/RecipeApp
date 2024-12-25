package uk.ac.tees.mad.ra.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.ra.data.Recipe
import uk.ac.tees.mad.ra.data.UserData
import uk.ac.tees.mad.ra.data.api.RetrofitInstance
import uk.ac.tees.mad.ra.ui.GoogleAuthUiClient

class RecipeViewModel : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    init {
        fetchRecipes()
    }

    private fun fetchRecipes() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getRecipes()
                Log.d("API RESPONSE", response.hits.toString())
                _recipes.value = response.hits.map { it.recipe }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun getUserData(googleAuthUiClient: GoogleAuthUiClient) {
        viewModelScope.launch {
            _userData.value = googleAuthUiClient.getCurrentUser()
        }
    }
}


