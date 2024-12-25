package uk.ac.tees.mad.ra.data

data class ApiResponse(
    val hits: List<Hit>
)

data class Hit(
    val recipe: Recipe
)
