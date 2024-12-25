package uk.ac.tees.mad.ra.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import uk.ac.tees.mad.ra.data.Recipe
import uk.ac.tees.mad.ra.ui.theme.yellow
import uk.ac.tees.mad.ra.viewmodels.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, googleAuthUiClient: GoogleAuthUiClient) {
    val viewModel: RecipeViewModel = viewModel()
    val userData = viewModel.userData.collectAsState()
    val recipes by viewModel.recipes.collectAsState(initial = emptyList())
    val loading by viewModel.loading.collectAsState(initial = true)

    LaunchedEffect(Unit) {
        viewModel.getUserData(googleAuthUiClient)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    Card(
                        onClick = {

                        },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(userData.value?.imageUrl),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    navController.navigate("profile")
                                }
                        )
                    }
                }
            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addRecipe")
                },
                modifier = Modifier
                    .padding(16.dp),
                containerColor = yellow,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Recipe")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Column(Modifier.padding(16.dp)) {

                    Text(
                        text = "Hello, ${userData.value?.username}!",
                        color = Color.Gray,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Black,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Make your own food, \nstay at ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = yellow,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("home")
                            }
                        },
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = "Search for recipes")
                        },
                        shape = RoundedCornerShape(20.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Add Recipe"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Add Recipe"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Popular Recipes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                        items(recipes) { recipe ->
                            RecipeItem(modifier = Modifier.fillMaxHeight(), recipe) {
                                val encodedUri = Uri.encode(recipe.uri)
                                navController.navigate("recipe/$encodedUri")
                            }
                        }
                    }
                }


            }
        }
    }
}

@Composable
fun RecipeItem(modifier: Modifier = Modifier, recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = yellow.copy(0.9f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(recipe.image),
                contentDescription = recipe.label,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = recipe.label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                minLines = 2
            )
        }
    }
}