package uk.ac.tees.mad.recipeapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController, googleAuthUiClient: GoogleAuthUiClient) {
    val scope = rememberCoroutineScope()
    Column {
        Text(text = "Home")
        Button(onClick = {
            scope.launch {
                googleAuthUiClient.signOutUser()
                navController.navigate("login")
            }
        }) {
            Text(text = "Sign out")
        }
    }
}