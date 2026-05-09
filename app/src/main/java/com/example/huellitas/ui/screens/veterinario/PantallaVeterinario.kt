package com.example.huellitas.ui.screens.veterinario

import android.app.Application
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellitas.R
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.viewmodel.EstadoCambioEstado
import com.example.huellitas.viewmodel.EstadoPanelVet
import com.example.huellitas.viewmodel.PanelVetViewModel

private val OrangeEnTratamiento = Color(0xFFFF8C00)
private val GreenRehabilitado = Color(0xFF00C853)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVeterinario(
    vetId: Int,
    alCerrarSesion: () -> Unit,
    alRegistrarAnimal: (vetId: Int) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: PanelVetViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                PanelVetViewModel(context.applicationContext as Application, vetId) as T
        }
    )

    val estadoAnimales by viewModel.estadoAnimales.collectAsState()
    val estadoCambio by viewModel.estadoCambio.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    // Recargar animales cada vez que la pantalla vuelve al frente (ON_RESUME),
    // esto garantiza que al regresar de "Registrar Paciente" la lista se actualiza.
    val lifecycleOwner = LocalContext.current.let {
        androidx.lifecycle.compose.LocalLifecycleOwner.current
    }
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarAnimales()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Estado del diálogo de foto de rehabilitación
    var animalParaRehabilitar by remember { mutableStateOf<AnimalDto?>(null) }
    var fotoRehabUri by remember { mutableStateOf<Uri?>(null) }

    val launcherFotoRehab = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fotoRehabUri = uri
    }

    LaunchedEffect(estadoCambio) {
        when (val s = estadoCambio) {
            is EstadoCambioEstado.Exito -> {
                snackbar.showSnackbar("✅ Animal rehabilitado. Ahora aparece en adopción.")
                viewModel.resetearEstadoCambio()
            }
            is EstadoCambioEstado.Error -> {
                snackbar.showSnackbar(s.mensaje)
                viewModel.resetearEstadoCambio()
            }
            else -> Unit
        }
    }

    val totalEnTratamiento = (estadoAnimales as? EstadoPanelVet.Exito)?.enTratamiento?.size ?: 0
    val totalRehabilitados = (estadoAnimales as? EstadoPanelVet.Exito)?.rehabilitados?.size ?: 0

    // ── Diálogo: foto de rehabilitación ──
    animalParaRehabilitar?.let { animal ->
        AlertDialog(
            onDismissRequest = {
                animalParaRehabilitar = null
                fotoRehabUri = null
            },
            title = { Text("Marcar como Rehabilitado", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "El animal aparecerá en el módulo de adopción. " +
                        "Puedes agregar una foto del animal rehabilitado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF49454F)
                    )
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0E6FF))
                            .border(2.dp, GradientStart.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { launcherFotoRehab.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoRehabUri != null) {
                            AsyncImage(
                                model = fotoRehabUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Outlined.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = GradientStart
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Foto rehabilitado (opcional)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GradientStart
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.marcarParaAdopcion(animal.id, fotoRehabUri)
                        animalParaRehabilitar = null
                        fotoRehabUri = null
                    },
                    enabled = estadoCambio !is EstadoCambioEstado.Cargando,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenRehabilitado)
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    animalParaRehabilitar = null
                    fotoRehabUri = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { alRegistrarAnimal(vetId) },
                containerColor = GradientStart,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar animal")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Panel Veterinario",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Gestión de pacientes",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_huellitas),
                        contentDescription = "Logo Huellitas",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                actions = {
                    IconButton(onClick = alCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GradientStart)
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F0FA))
                .padding(paddingInterno)
        ) {
            // ── Tarjetas de estadísticas ──
            Row(modifier = Modifier.fillMaxWidth()) {
                TarjetaEstadisticaVet(
                    valor = totalEnTratamiento.toString(),
                    etiqueta = "🤒 En tratamiento",
                    colorFondo = OrangeEnTratamiento,
                    modifier = Modifier.weight(1f)
                )
                TarjetaEstadisticaVet(
                    valor = totalRehabilitados.toString(),
                    etiqueta = "✅ Rehabilitados",
                    colorFondo = GreenRehabilitado,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📋", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = " Mis Pacientes (En Tratamiento)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OrangeEnTratamiento
                )
            }

            when (val s = estadoAnimales) {
                is EstadoPanelVet.Cargando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GradientStart)
                    }
                }
                is EstadoPanelVet.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(s.mensaje, color = Color(0xFF7A757F), textAlign = TextAlign.Center)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.cargarAnimales() },
                                colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
                            ) { Text("Reintentar") }
                        }
                    }
                }
                is EstadoPanelVet.Exito -> {
                    if (s.enTratamiento.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🐶", fontSize = 56.sp)
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "No tienes pacientes en tratamiento",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1D1A20)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Usa el botón + para registrar un animal\nque ingresa a tratamiento.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF7A757F),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        val cambioEnProceso = estadoCambio is EstadoCambioEstado.Cargando
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(s.enTratamiento) { animal ->
                                TarjetaPaciente(
                                    animal = animal,
                                    cambiando = cambioEnProceso,
                                    alMarcarAdopcion = { animalParaRehabilitar = animal }
                                )
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPaciente(
    animal: AnimalDto,
    cambiando: Boolean,
    alMarcarAdopcion: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!animal.imagenUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = animal.imagenUrl,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(OrangeEnTratamiento.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐶", fontSize = 24.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = animal.nombre?.takeIf { it.isNotBlank() } ?: "Sin nombre",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1D1A20)
                    )
                    animal.raza?.takeIf { it.isNotBlank() }?.let {
                        Text(text = it, style = MaterialTheme.typography.bodySmall, color = OrangeEnTratamiento)
                    }
                }
            }

            animal.descripcion?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = Color(0xFF49454F))
            }

            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = Color(0xFF7A757F)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = animal.ubicacion,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7A757F)
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = alMarcarAdopcion,
                enabled = !cambiando,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenRehabilitado),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (cambiando) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Marcar como Rehabilitado", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun TarjetaEstadisticaVet(
    valor: String,
    etiqueta: String,
    colorFondo: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = valor, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}
