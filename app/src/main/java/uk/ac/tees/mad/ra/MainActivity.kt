package uk.ac.tees.mad.ra

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import uk.ac.tees.mad.ra.ui.AddRecipeScreen
import uk.ac.tees.mad.ra.ui.EditProfileScreen
import uk.ac.tees.mad.ra.ui.GoogleAuthUiClient
import uk.ac.tees.mad.ra.ui.HomeScreen
import uk.ac.tees.mad.ra.ui.LoginScreen
import uk.ac.tees.mad.ra.ui.ProfileScreen
import uk.ac.tees.mad.ra.ui.RecipeDetailsScreen
import uk.ac.tees.mad.ra.ui.SplashScreen
import uk.ac.tees.mad.ra.ui.UserRecipeScreen
import uk.ac.tees.mad.ra.ui.theme.RecipeAppTheme

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
        composable("addRecipe") { AddRecipeScreen(onRecipeSaved = { navController.popBackStack() }) }
        composable("userRecipes") { UserRecipeScreen(navController = navController) }
        composable("profile") { ProfileScreen(navController, googleAuthUiClient) }
        composable("editProfile") {
            EditProfileScreen(
                navController = navController,
                googleAuthUiClient = googleAuthUiClient
            )
        }
    }
}