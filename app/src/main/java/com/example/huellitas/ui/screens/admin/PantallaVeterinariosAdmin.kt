package com.example.huellitas.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huellitas.network.dto.VeterinarioDto
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.viewmodel.EstadoVeterinarios
import com.example.huellitas.viewmodel.VeterinarioViewModel

private val ColorPurple = Color(0xFF6750A4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVeterinariosAdmin(
    alVolver: () -> Unit,
    alAgregarVeterinario: () -> Unit,
    viewModel: VeterinarioViewModel = viewModel()
) {
    val estado by viewModel.estadoLista.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Veterinarios",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Gestión de profesionales",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = alAgregarVeterinario,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar", fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F0FA))
                .padding(padding)
        ) {
            when (val s = estado) {
                is EstadoVeterinarios.Cargando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ColorPurple)
                    }
                }

                is EstadoVeterinarios.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = s.mensaje,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF7A757F)
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.cargarVeterinarios() },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorPurple)
                            ) { Text("Reintentar") }
                        }
                    }
                }

                is EstadoVeterinarios.Exito -> {
                    val vets = s.veterinarios
                    val cantidad = vets.size

                    // Banner informativo
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = ColorPurple.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = ColorPurple
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalHospital,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "$cantidad ${if (cantidad == 1) "veterinario registrado" else "veterinarios registrados"}",
                                    fontWeight = FontWeight.Bold,
                                    color = ColorPurple,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Gestiona la red de profesionales médicos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorPurple.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    if (vets.isEmpty()) {
                        // Estado vacío
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🩺", fontSize = 56.sp)
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "No hay veterinarios registrados",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(0xFF1D1A20)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Agrega el primer profesional médico para comenzar.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF7A757F)
                                )
                                Spacer(Modifier.height(24.dp))
                                Button(
                                    onClick = alAgregarVeterinario,
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorPurple),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Agregar Primer Veterinario")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(vets) { vet ->
                                TarjetaVeterinario(vet)
                            }
                            item { Spacer(Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaVeterinario(vet: VeterinarioDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = ColorPurple
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = vet.nombre.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vet.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1D1A20)
                )
                vet.especializacion?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorPurple
                    )
                }
                Spacer(Modifier.height(6.dp))
                vet.telefono?.takeIf { it.isNotBlank() }?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color(0xFF7A757F)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF7A757F)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF7A757F)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = vet.correo,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7A757F)
                    )
                }
            }
        }
    }
}
