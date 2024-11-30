package uk.ac.tees.mad.recipeapp.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.ac.tees.mad.recipeapp.data.FirestoreRecipe

class UserListedRecipesViewModel : ViewModel() {
    private val _recipesState = MutableStateFlow<List<FirestoreRecipe>>(emptyList())
    val recipesState = _recipesState.asStateFlow()

    private val _loadingState = MutableStateFlow(true)
    val loadingState = _loadingState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    init {
        fetchUserRecipes()
    }

    private fun fetchUserRecipes() {
        Firebase.firestore
            .collection("recipes")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(FirestoreRecipe::class.java)
                }
                _recipesState.value = recipes
                _loadingState.value = false
            }
            .addOnFailureListener { exception ->
                _snackbarMessage.value = "Failed to fetch recipes: ${exception.message}"
                _loadingState.value = false
            }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
