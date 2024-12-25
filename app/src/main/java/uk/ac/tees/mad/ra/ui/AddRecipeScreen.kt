package uk.ac.tees.mad.ra.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import uk.ac.tees.mad.ra.ui.theme.yellow
import uk.ac.tees.mad.ra.viewmodels.AddRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    onRecipeSaved: () -> Unit,
    viewModel: AddRecipeViewModel = viewModel()
) {
    val recipeNameState by viewModel.recipeNameState
    val ingredientsState by viewModel.ingredientsState
    val instructionsState by viewModel.instructionsState
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text("Add Recipe") },
                colors = TopAppBarDefaults.topAppBarColors(yellow),
                navigationIcon = {
                    IconButton(
                        onClick = { onRecipeSaved() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = recipeNameState,
                    onValueChange = viewModel::setRecipeName,
                    label = { Text("Recipe Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = ingredientsState,
                    onValueChange = viewModel::setIngredients,
                    label = { Text("Ingredients") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                TextField(
                    value = instructionsState,
                    onValueChange = viewModel::setInstructions,
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                Button(
                    onClick = {
                        viewModel.saveRecipe(
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Recipe saved successfully")
                                }
                                onRecipeSaved()
                            },
                            onFailure = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "${it}",
                                        "Error",
                                        withDismissAction = true,
                                        SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        yellow
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Save Recipe")
                    }
                }
            }
        }
    }
}
