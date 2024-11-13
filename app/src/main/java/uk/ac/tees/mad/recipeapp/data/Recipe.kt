package uk.ac.tees.mad.recipeapp.data

data class Recipe(
    val uri: String,
    val label: String,
    val image: String,
    val ingredientLines: List<String>,
    val url: String
)
