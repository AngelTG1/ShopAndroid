package com.shop.shop.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.shop.R
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},  // ✅ Valor por defecto
    onNavigateToRegister: () -> Unit = {},  // ✅ Valor por defecto
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    // Observar cambios en el estado de autenticación
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F4FD),  // Azul muy claro arriba
                        Color(0xFFF0E6FF),  // Púrpura muy claro medio
                        Color(0xFFFFE6F0)   // Rosa muy claro abajo
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello Again!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(bottom = 18.dp)
        )
        Text(
            text = "Welcome back you've\nbeen missed!",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 25.sp,
            modifier = Modifier.padding(bottom = 30.dp),
            color = Color(0xFF2D3748),
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Email",
                    tint = if (email.isNotEmpty()) colorResource(R.color.red_700) else Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),  // ✅ Esquinas redondeadas como el botón
            textStyle = TextStyle(color = colorResource(R.color.black)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.red_700),    // Mismo color que el botón
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedLabelColor = colorResource(R.color.red_700),
                unfocusedLabelColor = Color.Gray,
                cursorColor = colorResource(R.color.red_700),
                focusedLeadingIconColor = colorResource(R.color.red_700),
                unfocusedLeadingIconColor = Color.Gray
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Password",
                    tint = if (email.isNotEmpty()) colorResource(R.color.red_700) else Color.Gray
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            trailingIcon = {

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color.Gray.copy(alpha = 0.6f)
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(color = colorResource(R.color.black)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.red_700),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedLabelColor = colorResource(R.color.red_700),
                unfocusedLabelColor = Color.Gray,
                cursorColor = colorResource(R.color.red_700),
                focusedLeadingIconColor = colorResource(R.color.red_700),
                unfocusedLeadingIconColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login(email, password)
            },
            modifier = Modifier.fillMaxWidth().shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = colorResource(R.color.red_500).copy(alpha = 0.3f),
                spotColor = colorResource(R.color.red_500).copy(alpha = 0.3f)
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.red_700),
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(
                vertical = 20.dp
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White
                )
            } else {
                Text("Sign in")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToRegister,
            enabled = !uiState.isLoading,
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = colorResource(R.color.black),
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    )) {
                        append("Not a member?")
                    }
                    withStyle(style = SpanStyle(
                        color = colorResource(R.color.red_700),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 16.sp
                    )) {
                        append(" Register")
                    }
                }
            )
        }




        // Mostrar errores
        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// ✅ Preview separado y limpio
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}