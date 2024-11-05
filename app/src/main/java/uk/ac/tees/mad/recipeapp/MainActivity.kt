package uk.ac.tees.mad.recipeapp

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.recipeapp.ui.HomeScreen
import uk.ac.tees.mad.recipeapp.ui.LoginScreen
import uk.ac.tees.mad.recipeapp.ui.ProfileScreen
import uk.ac.tees.mad.recipeapp.ui.RecipeDetailScreen
import uk.ac.tees.mad.recipeapp.ui.SplashScreen
import uk.ac.tees.mad.recipeapp.ui.UserRecipeScreen
import uk.ac.tees.mad.recipeapp.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeAppTheme {
                NavigationGraph(navController = rememberNavController())
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("recipeDetails/{recipeId}") { backStackEntry ->
            RecipeDetailScreen(recipeId = backStackEntry.arguments?.getString("recipeId"))
        }
        composable("userRecipes") { UserRecipeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}