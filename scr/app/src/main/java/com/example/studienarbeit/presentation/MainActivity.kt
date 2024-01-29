package com.example.studienarbeit.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.studienarbeit.domain.repository.GoogleAuthRepository
import com.example.studienarbeit.presentation.addMarker.AddMarkerScreen
import com.example.studienarbeit.presentation.addMarker.AddMarkerViewModel
import com.example.studienarbeit.presentation.map.MapScreen
import com.example.studienarbeit.presentation.map.MapViewModel
import com.example.studienarbeit.presentation.permissions.PermissionScreen
import com.example.studienarbeit.presentation.permissions.PermissionViewModel
import com.example.studienarbeit.presentation.profile.ProfileScreen
import com.example.studienarbeit.presentation.signin.SignInScreen
import com.example.studienarbeit.presentation.signin.SignInViewModel
import com.example.studienarbeit.ui.theme.StudienarbeitTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthRepository

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            StudienarbeitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navigator = Navigator()

                    LaunchedEffect("navigation") {
                        navigator.sharedFlow.onEach {
                            navController.navigate(it.label)
                        }.launchIn(this)
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Navigator.NavTarget.SIGNIN.label
                    ) {
                        composable(Navigator.NavTarget.SIGNIN.label) {
                            val viewModel = hiltViewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedUser() != null) {
                                    navController.navigate(Navigator.NavTarget.PERMISSIONS.label)
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)

                                        }

                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignedUpSuccessful) {
                                if (state.isSignedUpSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate(Navigator.NavTarget.PERMISSIONS.label)
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                viewModel = viewModel,
                                onSignUpWithGoogle = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                })
                        }

                        composable(Navigator.NavTarget.PERMISSIONS.label) {
                            val viewModel = viewModel<PermissionViewModel>()
                            val permissionsGranted = viewModel.granted

                            if (hasAppPermissions())
                                navigator.navigateTo(Navigator.NavTarget.MAP)
                            else {
                                LaunchedEffect(permissionsGranted.value) {
                                    if (permissionsGranted.value)
                                        navigator.navigateTo(Navigator.NavTarget.MAP)
                                }
                                PermissionScreen(viewModel)
                            }
                        }
                        navigation(
                            startDestination = Navigator.NavTarget.MAP.label,
                            route = Navigator.NavTarget.ROOT.label
                        ) {

                            composable(Navigator.NavTarget.MAP.label) {

                                val viewModel = hiltViewModel<MapViewModel>()
                                MapScreen(
                                    viewModel = viewModel,
                                    navigateTo = {
                                        navController.navigate(it)
                                    }
                                )
                            }
                            composable(Navigator.NavTarget.ADD_MARKER.label + "/{latitude}/{longitude}") {
                                val arguments = it.arguments
                                val _latitude = arguments?.getString("latitude")
                                val _longitude = arguments?.getString("longitude")
                                val latitude = _latitude?.toDouble() ?: 0.0
                                val longitude = _longitude?.toDouble() ?: 0.0

                                val viewModel = hiltViewModel<AddMarkerViewModel>()
                                AddMarkerScreen(
                                    viewModel = viewModel,
                                    latitude = latitude,
                                    longitude = longitude,
                                    navigateBack = {
                                        navigator.navigateTo(Navigator.NavTarget.MAP)
                                    }
                                )
                            }

                            composable(Navigator.NavTarget.PROFILE.label) {
                                ProfileScreen(
                                    userData = googleAuthUiClient.getSignedUser(),
                                    onSignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(
                                                applicationContext,
                                                "Signed out",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            navController.navigate(Navigator.NavTarget.SIGNIN.label)
                                        }
                                    },
                                    navigateBack = {
                                        navigator.navigateTo(Navigator.NavTarget.MAP)
                                    }
                                )
                            }
                        }
                    }


                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.hasAppPermissions(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}




