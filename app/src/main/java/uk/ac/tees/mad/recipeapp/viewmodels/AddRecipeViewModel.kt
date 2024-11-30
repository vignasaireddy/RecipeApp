package uk.ac.tees.mad.recipeapp.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import uk.ac.tees.mad.recipeapp.data.FirestoreRecipe

class AddRecipeViewModel : ViewModel() {
    private val _recipeNameState = mutableStateOf("")
    val recipeNameState = _recipeNameState

    private val _ingredientsState = mutableStateOf("")
    val ingredientsState = _ingredientsState

    private val _instructionsState = mutableStateOf("")
    val instructionsState = _instructionsState

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    fun setRecipeName(name: String) {
        _recipeNameState.value = name
    }

    fun setIngredients(ingredients: String) {
        _ingredientsState.value = ingredients
    }

    fun setInstructions(instructions: String) {
        _instructionsState.value = instructions
    }

    fun saveRecipe(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        _isLoading.value = true
        val recipe = FirestoreRecipe(
            name = recipeNameState.value,
            ingredients = ingredientsState.value.split("\n"),
            instructions = instructionsState.value.split("\n")
        )
        val recipeDocument = Firebase.firestore
            .collection("recipes")
            .document()
        recipeDocument.set(recipe).addOnSuccessListener {
            _isLoading.value = false
            onSuccess.invoke()
        }.addOnFailureListener {
            _isLoading.value = false
            it.printStackTrace()
            onFailure.invoke(it.message ?: "Error saving")
        }
    }

}
