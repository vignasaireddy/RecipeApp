package uk.ac.tees.mad.recipeapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import uk.ac.tees.mad.recipeapp.data.Recipe
import uk.ac.tees.mad.recipeapp.ui.theme.yellow
import uk.ac.tees.mad.recipeapp.viewmodels.RecipeViewModel

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

                },
                modifier = Modifier
                    .padding(16.dp)
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
                    LazyColumn {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Popular Recipes",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(recipes) { recipe ->
                            RecipeItem(recipe)
                        }
                    }
                }


            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {

            }
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
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}