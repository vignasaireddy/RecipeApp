package uk.ac.tees.mad.ra.data

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?,
    var imageUrl: String? = null,
    val location: String? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "username" to username,
            "email" to email,
            "imageUrl" to "https://robohash.org/${email}",
            "location" to location
        )
    }
}