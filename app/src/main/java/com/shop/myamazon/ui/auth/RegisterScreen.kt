package com.shop.myamazon.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.myamazon.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},  // ✅ Valor por defecto
    onNavigateToLogin: () -> Unit = {},  // ✅ Valor por defecto
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onRegisterSuccess()
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 🎨 TÍTULO MODERNO
        Text(
            text = "Create Account",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Sign up to get started!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4A5568),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 👤 NOMBRE
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Nombre",
                    tint = if (name.isNotEmpty()) colorResource(R.color.red_700) else Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(16.dp),
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

        // 👤 APELLIDO
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("LastName") },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Apellido",
                    tint = if (lastName.isNotEmpty()) colorResource(R.color.red_700) else Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(16.dp),
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

        // 📧 EMAIL
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

        // 📱 TELÉFONO
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            leadingIcon = {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = "Teléfono",
                    tint = if (phone.isNotEmpty()) colorResource(R.color.red_700) else Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(16.dp),
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

        // 🔒 CONTRASEÑA
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.red_700),    // Mismo color que el botón
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedLabelColor = colorResource(R.color.red_700),
                unfocusedLabelColor = Color.Gray,
                cursorColor = colorResource(R.color.red_700),
                focusedLeadingIconColor = colorResource(R.color.red_700),
                unfocusedLeadingIconColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔒 CONFIRMAR CONTRASEÑA
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Confirmar contraseña",
                    tint = if (confirmPassword.isNotEmpty()) {
                        if (password == confirmPassword) colorResource(R.color.red_700) else Color.Red
                    } else Color.Gray
                )
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color.Gray.copy(alpha = 0.6f)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.red_700),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedLabelColor = if (password == confirmPassword && confirmPassword.isNotEmpty()) {
                    colorResource(R.color.red_700)
                } else if (password != confirmPassword && confirmPassword.isNotEmpty()) {
                    Color.Red
                } else Color.Gray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = colorResource(R.color.red_700),
                focusedLeadingIconColor = if (password == confirmPassword && confirmPassword.isNotEmpty()) {
                    colorResource(R.color.red_700)
                } else if (password != confirmPassword && confirmPassword.isNotEmpty()) {
                    Color.Red
                } else Color.Gray,
                unfocusedLeadingIconColor = Color.Gray,

            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔴 BOTÓN REGISTRAR
        Button(
            onClick = {
                if (password == confirmPassword) {
                    viewModel.register(
                        name = name,
                        lastName = lastName,
                        email = email,
                        password = password,
                        phone = phone.ifBlank { null }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = colorResource(R.color.red_700).copy(alpha = 0.3f),
                    spotColor = colorResource(R.color.red_700).copy(alpha = 0.3f)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.red_700),
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(vertical = 20.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White
                )
            } else {
                Text(
                    "Register",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📝 ENLACE LOGIN
        TextButton(
            onClick = onNavigateToLogin,
            enabled = !uiState.isLoading
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = colorResource(R.color.black),
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    )
                    ) {
                        append("Already have an account?")
                    }
                    withStyle(style = SpanStyle(
                        color = colorResource(R.color.red_700),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 16.sp
                    )
                    ) {
                        append(" Sign in")
                    }
                }
            )
        }


        // ❌ ERRORES
        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.15f)
                )
            ) {
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }
        }

        // ⚠️ ERROR CONTRASEÑAS NO COINCIDEN
        if (password != confirmPassword && confirmPassword.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.15f)
                )
            ) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ✅ PREVIEW SEPARADO Y LIMPIO
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen()
    }
}