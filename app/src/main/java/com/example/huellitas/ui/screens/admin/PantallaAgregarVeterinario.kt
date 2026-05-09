package com.example.huellitas.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.viewmodel.EstadoCrearVet
import com.example.huellitas.viewmodel.VeterinarioViewModel

private val ColorPurpleVet = Color(0xFF6750A4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgregarVeterinario(
    alVolver: () -> Unit,
    viewModel: VeterinarioViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var especializacion by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val estadoCrear by viewModel.estadoCrear.collectAsState()
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(estadoCrear) {
        when (val s = estadoCrear) {
            is EstadoCrearVet.Exito -> {
                snackbarState.showSnackbar("Veterinario registrado exitosamente.")
                viewModel.resetearEstadoCrear()
                alVolver()
            }
            is EstadoCrearVet.Error -> {
                snackbarState.showSnackbar(s.mensaje)
                viewModel.resetearEstadoCrear()
            }
            else -> Unit
        }
    }

    val cargando = estadoCrear is EstadoCrearVet.Cargando

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "+ Agregar Veterinario",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Registra un nuevo profesional médico",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GradientStart)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F0FA))
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Ícono central
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = ColorPurpleVet
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.LocalHospital,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Tarjeta del formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    CampoVeterinario(
                        valor = nombre,
                        alCambiar = { nombre = it },
                        etiqueta = "Nombre Completo",
                        icono = Icons.Outlined.Person,
                        placeholder = "Ej. Dr. Carlos Pérez"
                    )

                    Spacer(Modifier.height(12.dp))

                    CampoVeterinario(
                        valor = telefono,
                        alCambiar = { telefono = it },
                        etiqueta = "Teléfono (WhatsApp)",
                        icono = Icons.Outlined.Phone,
                        placeholder = "+57 300 000 0000",
                        tipo = KeyboardType.Phone
                    )
                    Text(
                        text = "Incluye el código del país para WhatsApp",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7A757F),
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    CampoVeterinario(
                        valor = correo,
                        alCambiar = { correo = it },
                        etiqueta = "Correo Electrónico",
                        icono = Icons.Outlined.Email,
                        placeholder = "correo@ejemplo.com",
                        tipo = KeyboardType.Email
                    )

                    Spacer(Modifier.height(12.dp))

                    CampoVeterinario(
                        valor = especializacion,
                        alCambiar = { especializacion = it },
                        etiqueta = "Especialización",
                        icono = Icons.Outlined.MedicalServices,
                        placeholder = "Ej. Cirugía, Dermatología..."
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = ColorPurpleVet
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ColorPurpleVet,
                            unfocusedTextColor = ColorPurpleVet,
                            cursorColor = ColorPurpleVet,
                            focusedBorderColor = ColorPurpleVet,
                            unfocusedBorderColor = Color(0xFFCAC4D0),
                            focusedLabelColor = ColorPurpleVet,
                            unfocusedLabelColor = Color(0xFF7A757F),
                            focusedContainerColor = Color(0xFFFAF5FF),
                            unfocusedContainerColor = Color(0xFFFAF5FF),
                            focusedPlaceholderColor = Color(0xFFB39DDB),
                            unfocusedPlaceholderColor = Color(0xFFB39DDB),
                            focusedLeadingIconColor = ColorPurpleVet,
                            unfocusedLeadingIconColor = ColorPurpleVet
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                TextButton(
                    onClick = alVolver,
                    modifier = Modifier.weight(1f),
                    enabled = !cargando
                ) {
                    Text("Cancelar", color = Color(0xFF7A757F))
                }

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick = {
                        viewModel.crearVeterinario(
                            nombre = nombre,
                            telefono = telefono,
                            correo = correo,
                            especializacion = especializacion,
                            password = password
                        )
                    },
                    modifier = Modifier.weight(2f),
                    enabled = !cargando,
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPurpleVet),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Guardar Veterinario", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CampoVeterinario(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    placeholder: String,
    tipo: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        placeholder = { Text(placeholder, color = Color(0xFFCAC4D0)) },
        leadingIcon = {
            Icon(imageVector = icono, contentDescription = null, tint = ColorPurpleVet)
        },
        keyboardOptions = KeyboardOptions(keyboardType = tipo),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ColorPurpleVet,
            unfocusedTextColor = ColorPurpleVet,
            cursorColor = ColorPurpleVet,
            focusedBorderColor = ColorPurpleVet,
            unfocusedBorderColor = Color(0xFFCAC4D0),
            focusedLabelColor = ColorPurpleVet,
            unfocusedLabelColor = Color(0xFF7A757F),
            focusedContainerColor = Color(0xFFFAF5FF),
            unfocusedContainerColor = Color(0xFFFAF5FF),
            focusedPlaceholderColor = Color(0xFFB39DDB),
            unfocusedPlaceholderColor = Color(0xFFB39DDB),
            focusedLeadingIconColor = ColorPurpleVet,
            unfocusedLeadingIconColor = ColorPurpleVet
        ),
        singleLine = true
    )
}
