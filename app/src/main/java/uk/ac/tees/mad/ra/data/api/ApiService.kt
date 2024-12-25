package uk.ac.tees.mad.ra.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import uk.ac.tees.mad.ra.data.ApiResponse

interface ApiService {


    @GET("api/recipes/v2")
    suspend fun getRecipes(
        @Query("type") type: String = "public",
        @Query("app_id") appId: String = APP_ID,
        @Query("app_key") appKey: String = APP_KEY,
        @Query("random") random: Boolean = true,
        @Query("diet") diet: String = "balanced",
    ): ApiResponse

    @GET("api/recipes/v2/by-uri")
    suspend fun getRecipeDetails(
        @Query("type") type: String = "public",
        @Query("uri") uri: String,
        @Query("app_id") appId: String = APP_ID,
        @Query("app_key") appKey: String = APP_KEY
    ): ApiResponse
}
