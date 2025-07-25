package com.shop.shop.ui.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shop.shop.R
import com.shop.shop.ui.components.CardSubs

// Data class para los planes
data class SubscriptionPlan(
    val id: String,
    val title: String,
    val plan: String,
    val price: String,
    val description: String,
    val features: List<String>,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onBack: () -> Unit = {},
    onNavigateToSubscriptionPlans: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Lista de planes de suscripción
    val subscriptionPlans = listOf(
        SubscriptionPlan(
            id = "monthly",
            title = "Premium",
            plan = "Mensual",
            price = "$5.00 por mes",
            description = "• Productos ilimitados\n• Análisis de ventas avanzado\n• Soporte prioritario 24/7\n• Herramientas de marketing\n• Sin comisiones adicionales",
            features = listOf(
                "Productos ilimitados",
                "Análisis de ventas avanzado",
                "Soporte prioritario 24/7",
                "Herramientas de marketing",
                "Sin comisiones adicionales"
            ),
            isPopular = true
        ),
        SubscriptionPlan(
            id = "yearly",
            title = "Premium Plus",
            plan = "Anual",
            price = "$50.00 por año",
            description = "• Todo lo del plan mensual\n• 2 meses gratis (ahorra $10)\n• Consultoría personalizada\n• Acceso anticipado a nuevas funciones\n• Reportes personalizados",
            features = listOf(
                "Todo lo del plan mensual",
                "2 meses gratis (ahorra $10)",
                "Consultoría personalizada",
                "Acceso anticipado a nuevas funciones",
                "Reportes personalizados"
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Planes de Suscripción",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A202C)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFF1A202C)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        // Contenido principal
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Encabezado
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Elige el plan perfecto para ti",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A202C),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Desbloquea todas las funciones y lleva tu negocio al siguiente nivel",
                            fontSize = 14.sp,
                            color = Color(0xFF718096),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Lista de planes
            items(subscriptionPlans) { plan ->
                Box {
                    CardSubs(
                        title = plan.title,
                        plan = plan.plan,
                        price = plan.price,
                        description = plan.description,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Badge "Más Popular" para el plan mensual
                    if (plan.isPopular) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-12).dp, y = (-8).dp)
                                .background(
                                    color = colorResource(R.color.red_700),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "MÁS POPULAR",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Información adicional
            item {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "💡 Información importante",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A202C)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "• Puedes cambiar o cancelar tu plan en cualquier momento\n• Todos los planes incluyen actualizaciones automáticas\n• Soporte técnico incluido en todos los planes\n• Garantía de satisfacción de 30 días",
                            fontSize = 14.sp,
                            color = Color(0xFF4A5568),
                            lineHeight = 20.sp
                        )
                    }
                
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionScreenPreview() {
    SubscriptionScreen()
}