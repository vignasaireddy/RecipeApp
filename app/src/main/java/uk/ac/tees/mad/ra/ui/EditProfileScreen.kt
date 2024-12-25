package uk.ac.tees.mad.ra.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import uk.ac.tees.mad.ra.viewmodels.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.ac.tees.mad.ra.ui.theme.yellow
import java.io.ByteArrayOutputStream
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    googleAuthUiClient: GoogleAuthUiClient,
    viewModel: ProfileViewModel = viewModel()
) {
    val userData = viewModel.userData.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val isUpdating by viewModel.updating.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf(userData.value?.username ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)
    val storagePermissionState =
        rememberPermissionState(permission = android.Manifest.permission.READ_EXTERNAL_STORAGE)

    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
        }

    val requestCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->

            bitmap?.let { bm ->
                val bytes = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val contentResolver = context.contentResolver
                val path =
                    MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
                imageUri = Uri.parse(path)

            }

        }

    LaunchedEffect(Unit) {
        viewModel.getUserData(googleAuthUiClient)
    }

    LaunchedEffect(userData.value) {
        name = userData.value?.username ?: ""
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Edit Profile") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Image(
                    painter = rememberAsyncImagePainter(imageUri ?: userData.value?.imageUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.run {
                        size(100.dp)
                            .clip(CircleShape)
                            .clickable {
                                showImagePickerOptions(
                                    context,
                                    cameraPermissionState,
                                    storagePermissionState,
                                    galleryLauncher,
                                    requestCameraLauncher
                                )
                            }
                            .border(2.dp, Color.Gray, CircleShape)
                            .align(Alignment.CenterHorizontally)
                    },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateUserProfile(name, imageUri) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(yellow)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun showImagePickerOptions(
    context: Context,
    cameraPermissionState: PermissionState,
    storagePermissionState: PermissionState,
    galleryLauncher: ActivityResultLauncher<String>,
    requestCameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>
) {
    val options = arrayOf("Camera", "Gallery")

    AlertDialog.Builder(context).setTitle("Select Image Source")
        .setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    if (cameraPermissionState.status.isGranted) {
                        requestCameraLauncher.launch(null)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }

                1 -> {

                        galleryLauncher.launch("image/*")



                }
            }
        }.show()
}

private fun getCameraOutputOptions(context: Context): Uri {
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val fileName = "IMG_${System.currentTimeMillis()}.jpg"
    val file = File(storageDir, fileName)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}