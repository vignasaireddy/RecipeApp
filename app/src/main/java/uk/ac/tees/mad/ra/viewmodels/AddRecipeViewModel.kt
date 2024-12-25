package uk.ac.tees.mad.ra.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import uk.ac.tees.mad.ra.data.FirestoreRecipe

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
        val map = mapOf(
            "name" to recipe.name,
            "ingredients" to recipe.ingredients,
            "instructions" to recipe.instructions,
            "userId" to Firebase.auth.currentUser?.uid
        )
        Firebase.firestore
            .collection("recipes")
            .document()
            .set(map)
            .addOnSuccessListener {
                _isLoading.value = false
                onSuccess.invoke()
            }.addOnFailureListener {
                _isLoading.value = false
                it.printStackTrace()
                onFailure.invoke(it.message ?: "Error saving")
            }
    }

}
