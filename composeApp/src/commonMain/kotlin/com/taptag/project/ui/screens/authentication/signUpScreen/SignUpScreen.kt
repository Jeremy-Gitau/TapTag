package com.taptag.project.ui.screens.authentication.signUpScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.ui.composables.authentication.signUp.SignUpContent
import com.taptag.project.ui.screens.authentication.UserScreenModel
import com.taptag.project.ui.screens.authentication.signInScreen.SignInScreen

@OptIn(ExperimentalMaterial3Api::class)
class SignUpScreen : Screen {

    @Composable
    override fun Content() {

        val authenticationScreenModel: UserScreenModel = koinScreenModel()
        val authState by authenticationScreenModel.state.collectAsState()

        var dismiss by remember { mutableStateOf(false) }

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(authState.error) {
            println("${authState.error}")
            println("${authState.currentUser}")
        }

        if (authState.error != null) {

            AlertDialog(
                onDismissRequest = { dismiss = false },
                confirmButton = {

                    Button(
                        onClick = { dismiss = false}
                    ) {
                        Text(
                            text = "Okay"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Error",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(
                        text = authState.error.toString()
                    )
                }
            )
        }

        if (authState.navigateToSignIn) {

            navigator.push(SignInScreen())

            println("${authState.currentUser}")

        }

        SignUpContent(
            onClickSignUp = authenticationScreenModel::signUpUser,
            state = authState,
            navigate = navigator
        )
    }
}