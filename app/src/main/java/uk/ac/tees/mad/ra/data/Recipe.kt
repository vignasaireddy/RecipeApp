package uk.ac.tees.mad.ra.data

data class Recipe(
    val uri: String,
    val label: String,
    val image: String,
    val ingredientLines: List<String>,
    val url: String,
    val totalTime: Double,
    val calories: Double,
    val totalWeight: Double,
    val cuisineType: List<String>,
    val dishType: List<String>,
    val mealType: List<String>,
)

data class FirestoreRecipe(
    val name: String,
    val ingredients: List<String>,
    val instructions: List<String>,
)
