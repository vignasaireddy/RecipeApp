package uk.ac.tees.mad.recipeapp.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import uk.ac.tees.mad.recipeapp.data.ApiResponse

interface ApiService {
    @GET("api/recipes/v2")
    suspend fun getRecipes(
        @Query("type") type: String = "public",
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("random") random: Boolean = true,
        @Query("diet") diet: String = "balanced",
    ): ApiResponse
}
