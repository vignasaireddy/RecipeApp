package uk.ac.tees.mad.recipeapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.recipeapp.data.Recipe
import uk.ac.tees.mad.recipeapp.data.api.RetrofitInstance

class RecipeViewModel : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    init {
        fetchRecipes()
    }
    private fun fetchRecipes() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getRecipes(
                    appId = "a99fe844",
                    appKey = "d3efe8d9604eead8938f4f2c231f1b48"
                )
                Log.d("API RESPONSE", response.hits.toString())
                _recipes.value = response.hits.map { it.recipe }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }
}
