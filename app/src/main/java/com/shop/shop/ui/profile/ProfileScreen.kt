package com.shop.shop.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.shop.data.remote.models.User
import com.shop.shop.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit = {},
    onNavigateToMyProducts: () -> Unit = {},
    onNavigateToMySubscription: () -> Unit = {}, // ✅ Nuevo parámetro
    onNavigateToSubscriptionPlans: () -> Unit = {}, // ✅ Nuevo parámetro
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ✅ Verificar si puede gestionar productos
    val canManageProducts = uiState.user?.role?.let { role ->
        role.equals("memberships", ignoreCase = true) ||
                role.equals("Admin", ignoreCase = true) ||
                role.equals("Premium", ignoreCase = true) ||
                role.equals("Vendedor", ignoreCase = true)
    } ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFC))
            .verticalScroll(rememberScrollState())
    ) {
        // Header con avatar y nombre
        ProfileHeader(
            user = uiState.user,
            onBack = onBack
        )

        // Información personal
        ProfileSection(
            title = "Información Personal",
            content = {
                uiState.user?.let { user ->
                    ProfileInfoItem(
                        icon = Icons.Default.Person,
                        label = "Nombre completo",
                        value = "${user.name} ${user.lastName}"
                    )
                    ProfileInfoItem(
                        icon = Icons.Default.Email,
                        label = "Correo electrónico",
                        value = user.email
                    )
                    user.phone?.let { phone ->
                        ProfileInfoItem(
                            icon = Icons.Default.Phone,
                            label = "Teléfono",
                            value = phone
                        )
                    }
                    ProfileInfoItem(
                        icon = Icons.Default.Badge,
                        label = "Rol",
                        value = user.role
                    )
                    ProfileInfoItem(
                        icon = Icons.Default.Key,
                        label = "ID de usuario",
                        value = user.uuid
                    )
                }
            }
        )

        // Opciones de cuenta
        ProfileSection(
            title = "Configuración de Cuenta",
            content = {
                // ✅ Opción "Mis Productos" solo para usuarios Premium+
                if (canManageProducts) {
                    ProfileOptionItem(
                        icon = Icons.Default.Store,
                        title = "Mis Productos",
                        subtitle = "Gestiona tus productos publicados",
                        onClick = onNavigateToMyProducts
                    )
                }

                ProfileOptionItem(
                    icon = Icons.Default.Edit,
                    title = "Editar Perfil",
                    subtitle = "Actualiza tu información personal",
                    onClick = { /* TODO: Implementar edición */ }
                )
                ProfileOptionItem(
                    icon = Icons.Default.Lock,
                    title = "Cambiar Contraseña",
                    subtitle = "Modifica tu contraseña de acceso",
                    onClick = { /* TODO: Implementar cambio de contraseña */ }
                )
                ProfileOptionItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones",
                    subtitle = "Gestiona tus preferencias de notificación",
                    onClick = { /* TODO: Implementar notificaciones */ }
                )
            }
        )

        // Opciones generales
        ProfileSection(
            title = "General",
            content = {
                ProfileOptionItem(
                    icon = Icons.Default.Help,
                    title = "Ayuda y Soporte",
                    subtitle = "¿Necesitas ayuda? Estamos aquí",
                    onClick = { /* TODO: Implementar ayuda */ }
                )
                ProfileOptionItem(
                    icon = Icons.Default.Info,
                    title = "Acerca de",
                    subtitle = "Información sobre la aplicación",
                    onClick = { /* TODO: Implementar acerca de */ }
                )
                ProfileOptionItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Cerrar Sesión",
                    subtitle = "Salir de tu cuenta",
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    isDestructive = true
                )
            }
        )

        // Espaciado final
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileHeader(
    user: User?,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón de volver
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFF4A5568)
                    )
                }
            }

            // Avatar grande
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53E3E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.name?.take(1)?.uppercase() ?: "U",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            Text(
                text = "${user?.name ?: ""} ${user?.lastName ?: ""}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                textAlign = TextAlign.Center
            )

            // Rol del usuario
            Text(
                text = user?.role ?: "Cliente",
                fontSize = 16.sp,
                color = Color(0xFF718096),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF4A5568),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF718096),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color(0xFF2D3748),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) Color(0xFFE53E3E) else Color(0xFF4A5568),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) Color(0xFFE53E3E) else Color(0xFF2D3748)
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF718096)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = Color(0xFF718096),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val sampleUser = User(
        id = 1,
        uuid = "abc123def456",
        name = "Vanessa",
        lastName = "García",
        email = "vanessa@example.com",
        phone = "+52 961 123 4567",
        role = "Cliente"
    )

    // Simular AuthViewModel con datos de muestra
    ProfileHeader(user = sampleUser, onBack = {})
}