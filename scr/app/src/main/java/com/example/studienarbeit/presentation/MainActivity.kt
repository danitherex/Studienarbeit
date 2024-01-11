package com.example.studienarbeit.presentation

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.core.app.ActivityCompat
import androidx.datastore.dataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.studienarbeit.domain.repository.GoogleAuthRepository
import com.example.studienarbeit.presentation.map.MapScreen
import com.example.studienarbeit.presentation.map.MapViewModel
import com.example.studienarbeit.presentation.profile.ProfileScreen
import com.example.studienarbeit.presentation.signin.SignInScreen
import com.example.studienarbeit.presentation.signin.SignInViewModel
import com.example.studienarbeit.settings.AppSettingsSerializer
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

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO: make better permission handling https://www.youtube.com/watch?v=D3JCtaK8LSU
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ),
                0
            )
        }



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
                        startDestination = Navigator.NavTarget.LOGIN.label
                    ) {
                        composable(Navigator.NavTarget.LOGIN.label) {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedUser() != null) {
                                    navController.navigate(Navigator.NavTarget.MAP.label)
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

                            LaunchedEffect(key1 = state.isSignedInSuccessfull) {
                                if (state.isSignedInSuccessfull) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate(Navigator.NavTarget.MAP.label)
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
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
                        navigation(
                            startDestination = Navigator.NavTarget.MAP.label,
                            route = Navigator.NavTarget.ROOT.label
                        ) {

                            composable(Navigator.NavTarget.MAP.label) {

                                val viewModel = hiltViewModel<MapViewModel>()
                                MapScreen(
                                    viewModel = viewModel,
                                    navigateTo = {
                                        navigator.navigateTo(Navigator.NavTarget.PROFILE)
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

                                            navController.navigate(Navigator.NavTarget.LOGIN.label)
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




