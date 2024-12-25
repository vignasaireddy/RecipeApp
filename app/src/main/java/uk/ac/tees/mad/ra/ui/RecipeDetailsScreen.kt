package uk.ac.tees.mad.ra.ui

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.BreakfastDining
import androidx.compose.material.icons.outlined.FoodBank
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.ac.tees.mad.ra.ui.theme.yellow
import uk.ac.tees.mad.ra.viewmodels.RecipeDetailsViewModel
import uk.ac.tees.mad.ra.viewmodels.TimerState
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RecipeDetailsScreen(uri: String?, onBack: () -> Unit) {
    val viewModel: RecipeDetailsViewModel = viewModel()
    val recipe by viewModel.recipeDetails.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val context = LocalContext.current
    var remainingTimer by remember {
        mutableStateOf(0L)
    }
    var formattedTimer by remember {
        mutableStateOf("00:00")
    }
    LaunchedEffect(timerState) {
        when (timerState) {
            is TimerState.Running -> {
                val remainingTime = (timerState as TimerState.Running).timeRemaining

                Log.d("Timer", "Remaining Time: $remainingTime")
                remainingTimer = remainingTime
                while (remainingTimer > 0) {
                    delay(1000)
                    remainingTimer -= 1000
                    formattedTimer = "${remainingTimer / 1000 / 60}:${(remainingTimer / 1000) % 60}"
                }
            }

            else -> {

            }
        }

    }

    val notificationPermissionState =
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()
    val df = DecimalFormat("#.##")
    LaunchedEffect(Unit) {
        viewModel.fetchRecipeDetails(uri)
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Button(
                onClick = {
                    if (recipe?.totalTime != null) {
                        if (recipe?.totalTime == 0.0) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Can't start timer for this recipe as no time provided from API",
                                    actionLabel = "Error",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (notificationPermissionState.status.isGranted) {
                                    viewModel.startTimer(
                                        recipeName = recipe?.label ?: "",
                                        duration = recipe?.totalTime ?: 0.0,
                                        context = context
                                    )
                                } else {
                                    notificationPermissionState.launchPermissionRequest()
                                }
                            } else {
                                viewModel.startTimer(
                                    recipeName = recipe?.label ?: "",
                                    duration = recipe?.totalTime ?: 0.0,
                                    context = context
                                )
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Can't start timer for this recipe as no time provided from API",
                                actionLabel = "Error",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = yellow, contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Timer",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                when (timerState) {
                    is TimerState.Stopped -> Text("Start Timer")
                    is TimerState.Running -> {

                        Text("Remaining Time: ${(formattedTimer)} minutes")
                    }


                    is TimerState.Finished -> Text("Timer Finished")
                }
            }
        }
    ) { paddingValues ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = yellow)
                }
                Text(
                    text = recipe?.label ?: "Error",
                    style = MaterialTheme.typography.headlineSmall,
                    color = yellow
                )
            }

            Image(
                painter = rememberAsyncImagePainter(recipe?.image),
                contentDescription = recipe?.label,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DetailsComponent(
                        icon = Icons.Outlined.Timer,
                        text = if (recipe?.totalTime?.equals(0.0) == true) "N/A" else "${recipe?.totalTime}",
                        desc = "mins"
                    )
                    DetailsComponent(
                        icon = Icons.Outlined.LocalFireDepartment,
                        text = df.format(recipe?.calories ?: 0.0),
                        desc = "Cal"
                    )
                    DetailsComponent(
                        icon = Icons.Outlined.FoodBank,
                        text = df.format(recipe?.totalWeight ?: 0.0),
                        desc = "g"
                    )
                    DetailsComponent(
                        icon = Icons.Outlined.BreakfastDining,
                        text = recipe?.dishType?.get(0)?.replaceFirstChar { ch ->
                            ch.uppercase()
                        } ?: "N/A",
                        desc = ""
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(yellow)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cuisine Type:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${
                                recipe?.cuisineType?.joinToString(", ") { item ->
                                    item.replaceFirstChar { ch ->
                                        ch.uppercase()
                                    }
                                }
                            }",
                            style = MaterialTheme.typography.titleMedium,
                            color = yellow
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(yellow)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Meal Type:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = " ${
                                recipe?.mealType?.map { item ->
                                    item.replaceFirstChar { ch ->
                                        ch.uppercase()
                                    }
                                }?.joinToString(", ")
                            }",
                            style = MaterialTheme.typography.titleMedium,
                            color = yellow
                        )
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ingredients:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                recipe?.ingredientLines?.forEach { ingredient ->
                    Text(text = "- $ingredient", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Instructions:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                recipe?.url?.let {
                    TextButton(onClick = {
                        uriHandler.openUri(it)
                    }) {
                        Text(
                            text = recipe?.url ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

            }


        }
    }
}

@Composable
fun DetailsComponent(
    icon: ImageVector,
    text: String,
    desc: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(yellow)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = desc, style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

