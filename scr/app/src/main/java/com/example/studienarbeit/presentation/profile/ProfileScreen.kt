package com.example.studienarbeit.presentation.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.studienarbeit.presentation.profile.components.MarkersTable
import com.example.studienarbeit.presentation.signin.UserData
import com.example.studienarbeit.ui.theme.StudienarbeitTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: ProfileViewModel
) {

    val items = viewModel.items.collectAsStateWithLifecycle()
    val isFocused = viewModel.isFocused.collectAsStateWithLifecycle()



    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Gray)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if(isFocused.value) {
                if (userData?.profilePictureUrl != null) {
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Canvas(
                        modifier = Modifier
                            .size(150.dp),
                        onDraw = {
                            drawCircle(color = Color.Gray)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (userData?.username != null) {
                    Text(
                        text = userData.username,
                        textAlign = TextAlign.Center,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Button(
                    onClick = onSignOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Sign out")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
            MarkersTable(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                items = items.value,
                search = viewModel::searchMarkers,
                userID = viewModel.auth?.currentUser?.uid ?: "",
                deleteMarker = viewModel::deleteMarker,
                toggleFocus = viewModel::setFocus,
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    device = Devices.PIXEL_4_XL,
    showSystemUi = false,
    name = "Light Mode",
    group = "SignInScreen"
)
fun PreviewProfileScreen() {
    StudienarbeitTheme(darkTheme = false, dynamicColor = false) {
        val userdata = UserData("id", "username", null)
        ProfileScreen(userData = userdata, onSignOut = {}, navigateBack = {},
            viewModel = ProfileViewModel(
                useCases = null,
                auth = null
            )
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    device = Devices.PIXEL_4_XL,
    showSystemUi = false,
    name = "Dark Mode",
    group = "SignInScreen"
)
fun PreviewProfileScreen2() {
    StudienarbeitTheme(darkTheme = true, dynamicColor = false) {
        val userdata = UserData("id", "username", null)
        ProfileScreen(userData = userdata, onSignOut = {}, navigateBack = {},
            viewModel = ProfileViewModel(
                useCases = null,
                auth = null
            )
        )
    }
}