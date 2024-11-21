package uk.ac.tees.mad.recipeapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.recipeapp.data.Recipe
import uk.ac.tees.mad.recipeapp.data.api.RetrofitInstance

class RecipeDetailsViewModel : ViewModel() {
    private val _recipeDetails = MutableStateFlow<Recipe?>(null)
    val recipeDetails = _recipeDetails.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun fetchRecipeDetails(uri: String?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitInstance.api.getRecipeDetails(
                    uri = uri ?: ""
                )
                Log.d("URI", "URI: $uri")
                Log.d("RecipeDetailsViewModel", "fetchRecipeDetails: $response")
                _recipeDetails.value = response.hits.firstOrNull()?.recipe
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }
}
