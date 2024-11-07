package uk.ac.tees.mad.recipeapp.data

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "username" to username,
            "email" to email,
            "imageUrl" to "https://robohash.org/${email}"
        )
    }
}