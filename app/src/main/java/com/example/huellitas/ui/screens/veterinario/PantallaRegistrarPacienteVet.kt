package com.example.huellitas.ui.screens.veterinario

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Pets
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.ui.screens.camera.PantallaCamara
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.viewmodel.EstadoAnimalesDisponibles
import com.example.huellitas.viewmodel.EstadoRegistroPaciente
import com.example.huellitas.viewmodel.RegistrarPacienteVetViewModel
import kotlinx.coroutines.launch

// Número de contacto fijo de la organización para adopciones y reportes
private const val CONTACTO_ORGANIZACION = "+57 320 3717031"

private val ColorVet = GradientStart
private val ColorVerde = Color(0xFF00C853)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistrarPacienteVet(
    vetId: Int,
    alVolver: () -> Unit,
    alRegistrado: () -> Unit,
    viewModel: RegistrarPacienteVetViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val estadoDisponibles by viewModel.estadoDisponibles.collectAsState()
    val estadoRegistro by viewModel.estadoRegistro.collectAsState()
    val fotoIngresoUri by viewModel.fotoIngresoUri.collectAsState()

    val snackbar = remember { SnackbarHostState() }
    var tabSeleccionado by remember { mutableIntStateOf(0) }

    // Campos del formulario de nuevo paciente
    var nombre by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    // Controla si se muestra la cámara integrada en lugar del formulario
    var mostrarCamara by remember { mutableStateOf(false) }

    // Lanzador para pedir permiso de cámara
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mostrarCamara = true
        } else {
            scope.launch { snackbar.showSnackbar("Se necesita permiso de cámara para tomar fotos.") }
        }
    }

    // Lanzador galería (usado desde dentro de PantallaCamara)
    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.seleccionarFotoIngreso(uri)
        mostrarCamara = false
    }

    /** Abre la cámara integrada solicitando permiso si es necesario. */
    fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mostrarCamara = true
        } else {
            lanzadorPermiso.launch(Manifest.permission.CAMERA)
        }
    }

    // Reaccionar al resultado del registro/asignación
    LaunchedEffect(estadoRegistro) {
        when (val s = estadoRegistro) {
            is EstadoRegistroPaciente.Exito -> alRegistrado()
            is EstadoRegistroPaciente.Error -> {
                snackbar.showSnackbar(s.mensaje)
                viewModel.resetearEstado()
            }
            else -> Unit
        }
    }

    val enviando = estadoRegistro is EstadoRegistroPaciente.Enviando

    // ── Si la cámara está activa, reemplaza toda la pantalla ──
    if (mostrarCamara) {
        PantallaCamara(
            alCapturarFoto = { uri ->
                viewModel.seleccionarFotoIngreso(uri)
                mostrarCamara = false
            },
            alSeleccionarGaleria = { lanzadorGaleria.launch("image/*") },
            alCerrar = { mostrarCamara = false }
        )
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Registrar Paciente", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorVet)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F0FA))
                .padding(padding)
        ) {
            // ── Selector de tabs ──
            TabRow(
                selectedTabIndex = tabSeleccionado,
                containerColor = ColorVet,
                contentColor = Color.White
            ) {
                Tab(
                    selected = tabSeleccionado == 0,
                    onClick = { tabSeleccionado = 0 },
                    text = { Text("🐕 Perro existente") }
                )
                Tab(
                    selected = tabSeleccionado == 1,
                    onClick = { tabSeleccionado = 1 },
                    text = { Text("➕ Nuevo perro") }
                )
            }

            if (tabSeleccionado == 0) {
                // ── Tab 1: Perros reportados por usuarios sin vet asignado ──
                ContenidoPerrosExistentes(
                    estado = estadoDisponibles,
                    enviando = enviando,
                    onReintentar = { viewModel.cargarDisponibles() },
                    onAsignar = { idAnimal -> viewModel.asignarAnimalExistente(idAnimal, vetId) }
                )
            } else {
                // ── Tab 2: Registrar un perro nuevo directamente ──
                FormularioNuevoPaciente(
                    nombre = nombre,
                    raza = raza,
                    descripcion = descripcion,
                    ubicacion = ubicacion,
                    fotoUri = fotoIngresoUri,
                    enviando = enviando,
                    onNombre = { nombre = it },
                    onRaza = { raza = it },
                    onDescripcion = { descripcion = it },
                    onUbicacion = { ubicacion = it },
                    onAbrirCamara = { abrirCamara() },
                    onRegistrar = {
                        viewModel.registrarNuevoPaciente(
                            vetId       = vetId,
                            nombre      = nombre,
                            raza        = raza,
                            descripcion = descripcion,
                            ubicacion   = ubicacion,
                            contacto    = CONTACTO_ORGANIZACION
                        )
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Contenido Tab 1: lista de perros existentes
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ContenidoPerrosExistentes(
    estado: EstadoAnimalesDisponibles,
    enviando: Boolean,
    onReintentar: () -> Unit,
    onAsignar: (Int) -> Unit
) {
    when (estado) {
        is EstadoAnimalesDisponibles.Cargando -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ColorVet)
            }
        }
        is EstadoAnimalesDisponibles.Error -> {
            Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(estado.mensaje, textAlign = TextAlign.Center, color = Color(0xFF7A757F))
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onReintentar,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorVet)
                    ) { Text("Reintentar") }
                }
            }
        }
        is EstadoAnimalesDisponibles.Exito -> {
            if (estado.animales.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🐾", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No hay perros reportados\nsin veterinario asignado",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1A20)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Los perros que reportan los usuarios\naparecerán aquí para que los tomes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7A757F),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(Modifier.height(8.dp)) }
                    item {
                        Text(
                            "Toca un perro para tomarlo como paciente:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF7A757F)
                        )
                    }
                    items(estado.animales) { animal ->
                        TarjetaAnimalDisponible(
                            animal = animal,
                            asignando = enviando,
                            alAsignar = { onAsignar(animal.id) }
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Formulario Tab 2: nuevo paciente
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FormularioNuevoPaciente(
    nombre: String,
    raza: String,
    descripcion: String,
    ubicacion: String,
    fotoUri: Uri?,
    enviando: Boolean,
    onNombre: (String) -> Unit,
    onRaza: (String) -> Unit,
    onDescripcion: (String) -> Unit,
    onUbicacion: (String) -> Unit,
    onAbrirCamara: () -> Unit,
    onRegistrar: () -> Unit
) {
    val colores = OutlinedTextFieldDefaults.colors(
        focusedTextColor = ColorVet,
        unfocusedTextColor = ColorVet,
        cursorColor = ColorVet,
        focusedBorderColor = ColorVet,
        unfocusedBorderColor = Color(0xFFCAC4D0),
        focusedLabelColor = ColorVet,
        unfocusedLabelColor = Color(0xFF7A757F),
        focusedContainerColor = Color(0xFFFAF5FF),
        unfocusedContainerColor = Color(0xFFFAF5FF),
        focusedPlaceholderColor = Color(0xFFB39DDB),
        unfocusedPlaceholderColor = Color(0xFFB39DDB)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Datos del nuevo paciente",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1D1A20)
        )

        // ── Zona de foto de ingreso: toca para abrir la cámara integrada ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF0E6FF))
                .border(2.dp, ColorVet.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .clickable { onAbrirCamara() },
            contentAlignment = Alignment.Center
        ) {
            if (fotoUri != null) {
                // Vista previa de la foto seleccionada
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto de ingreso",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                // Indicador para volver a cambiar la foto
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Cambiar foto",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            } else {
                // Placeholder cuando no hay foto
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = ColorVet
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Toca para tomar la foto de ingreso",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorVet,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "(también puedes elegir de la galería)",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorVet.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        OutlinedTextField(
            value = nombre,
            onValueChange = onNombre,
            label = { Text("Nombre (si tiene)") },
            placeholder = { Text("Ej. Firulais, Toby...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = colores,
            singleLine = true
        )

        OutlinedTextField(
            value = raza,
            onValueChange = onRaza,
            label = { Text("Raza") },
            placeholder = { Text("Ej. Mestizo, Labrador...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = colores,
            singleLine = true
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = onDescripcion,
            label = { Text("Descripción / Condición de llegada") },
            placeholder = { Text("Ej. Llegó herido en la pata izquierda...") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = colores,
            maxLines = 4
        )

        OutlinedTextField(
            value = ubicacion,
            onValueChange = onUbicacion,
            label = { Text("Ubicación *") },
            placeholder = { Text("Lugar donde fue encontrado") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = colores,
            singleLine = true
        )

        // El contacto se asigna automáticamente al número de la organización;
        // se muestra como campo de solo lectura para transparencia.
        OutlinedTextField(
            value = CONTACTO_ORGANIZACION,
            onValueChange = {},
            label = { Text("Contacto para adopción") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = colores,
            enabled = false,
            singleLine = true,
            supportingText = {
                Text(
                    "Número fijo de WhatsApp de la organización",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7A757F)
                )
            }
        )

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = onRegistrar,
            enabled = !enviando,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorVet),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (enviando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Outlined.Pets, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Registrar Paciente", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tarjeta de animal disponible (Tab 1)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaAnimalDisponible(
    animal: AnimalDto,
    asignando: Boolean,
    alAsignar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto o placeholder
            if (!animal.imagenUrl.isNullOrBlank()) {
                AsyncImage(
                    model = animal.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF0E6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🐶", fontSize = 22.sp)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.nombre?.takeIf { it.isNotBlank() } ?: "Sin nombre",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1D1A20),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                animal.raza?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = GradientStart
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(11.dp),
                        tint = Color(0xFF7A757F)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = animal.ubicacion,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7A757F),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = alAsignar,
                enabled = !asignando,
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp)
            ) {
                if (asignando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Tomar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
