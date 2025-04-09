package com.taptag.project.ui.composables.authentication.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.ui.screens.authentication.UserState
import com.taptag.project.ui.screens.authentication.signInScreen.SignInScreen
import kotlin.reflect.KFunction1

@Composable
fun SignUpContent(
    onClickSignUp: KFunction1<AuthRequestDomain, Unit>,
    state: UserState,
    navigate: Navigator
) {
    var firstName by remember { mutableStateOf("") }
    var secondName by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo Circle
//            Box(
//                modifier = Modifier
//                    .size(64.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFF10B981)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Person,
//                    contentDescription = null,
//                    tint = Color.White,
//                    modifier = Modifier.size(32.dp)
//                )
//            }

            // Header
            Text(
                text = "Create an account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Enter your information to get started with digital business cards",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            // Form Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    placeholder = { Text("John") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = secondName,
                    onValueChange = { secondName = it },
                    label = { Text("Last Name") },
                    placeholder = { Text("Doe") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company") },
                placeholder = { Text("Your Company, Inc.") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Work Email") },
                placeholder = { Text("name@company.com") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Terms and Conditions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it }
                )
                Text(
                    text = "I agree to the terms of service and privacy policy",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Sign Up Button
            Button(
                onClick = {
                    onClickSignUp(
                        AuthRequestDomain(
                            firstName = firstName,
                            secondName = secondName,
                            company = company,
                            email = email,
                            workEmail = email,
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                )
            ) {
                Text("Create account")
            }

            // Sign In Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Already have an account? ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                TextButton(
                    onClick = { navigate.push(SignInScreen()) }
                ) {
                    Text(
                        "Sign in",
                        fontSize = 14.sp,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

    }
}