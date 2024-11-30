package uk.ac.tees.mad.recipeapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import uk.ac.tees.mad.recipeapp.ui.AddRecipeScreen
import uk.ac.tees.mad.recipeapp.ui.GoogleAuthUiClient
import uk.ac.tees.mad.recipeapp.ui.HomeScreen
import uk.ac.tees.mad.recipeapp.ui.LoginScreen
import uk.ac.tees.mad.recipeapp.ui.ProfileScreen
import uk.ac.tees.mad.recipeapp.ui.RecipeDetailsScreen
import uk.ac.tees.mad.recipeapp.ui.SplashScreen
import uk.ac.tees.mad.recipeapp.ui.UserRecipeScreen
import uk.ac.tees.mad.recipeapp.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeAppTheme {
                val navController = rememberNavController()
                NavigationGraph(navController = navController)
            }
        }
    }


}

@Composable
fun NavigationGraph(navController: NavHostController) {
    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(Identity.getSignInClient(context))
    }
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController, googleAuthUiClient) }
        composable("home") { HomeScreen(navController, googleAuthUiClient) }
        composable(
            route = "recipe/{uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            val decodedUri = uri?.let { Uri.decode(it) }
            RecipeDetailsScreen(
                uri = decodedUri,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("addRecipe") { AddRecipeScreen(onRecipeSaved = { navController.popBackStack() })}
        composable("userRecipes") { UserRecipeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}