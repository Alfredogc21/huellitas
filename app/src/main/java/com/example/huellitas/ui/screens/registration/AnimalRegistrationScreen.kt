package com.example.huellitas.ui.screens.registration

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellitas.model.TipoAnimal
import com.example.huellitas.ui.screens.camera.PantallaCamara
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import com.example.huellitas.ui.theme.PurpleDark
import com.example.huellitas.ui.theme.PurpleText
import com.example.huellitas.viewmodel.AnimalRegistroViewModel
import com.example.huellitas.viewmodel.EstadoRegistro
import kotlinx.coroutines.launch

/**
 * Pantalla de formulario para registrar un nuevo animal callejero.
 * Envía los datos al backend PHP mediante el ViewModel.
 *
 * @param alCompletarRegistro Callback que se invoca tras un registro exitoso o al presionar volver
 * @param viewModel ViewModel que gestiona el envío a la API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroAnimal(
    alCompletarRegistro: () -> Unit,
    viewModel: AnimalRegistroViewModel = viewModel()
) {
    // ── Estado del formulario ──
    var nombreAnimal by rememberSaveable { mutableStateOf("") }
    var tipoSeleccionado by rememberSaveable { mutableStateOf(TipoAnimal.PERRO) }
    var raza by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var ubicacion by rememberSaveable { mutableStateOf("") }
    var contacto by rememberSaveable { mutableStateOf("") }

    // ── Estado de cámara y permisos ──
    var mostrarCamara by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val estadoRegistro by viewModel.estadoRegistro.collectAsState()
    val imagenUri by viewModel.imagenUri.collectAsState()
    val estandoEnviando = estadoRegistro is EstadoRegistro.Enviando
    val snackbarHostState = remember { SnackbarHostState() }

    // Lanzador para seleccionar imagen de la galería (desde la cámara)
    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.seleccionarImagen(uri)
        mostrarCamara = false
    }

    // Lanzador para solicitar permiso de cámara
    val lanzadorPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mostrarCamara = true
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Se necesita permiso de cámara para tomar fotos")
            }
        }
    }

    /** Abre la cámara si el permiso ya fue concedido, o lo solicita. */
    fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mostrarCamara = true
        } else {
            lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    // Reaccionar a cambios del estado de registro
    LaunchedEffect(estadoRegistro) {
        when (val estado = estadoRegistro) {
            is EstadoRegistro.Exito -> {
                alCompletarRegistro()
                viewModel.reiniciarEstado()
            }
            is EstadoRegistro.Error -> {
                snackbarHostState.showSnackbar(estado.mensaje)
                viewModel.reiniciarEstado()
            }
            else -> Unit
        }
    }

    // ──────────────────────────────────────────────
    //  Si mostrarCamara → muestra la cámara integrada
    //  Si no → muestra el formulario de registro
    // ──────────────────────────────────────────────
    if (mostrarCamara) {
        PantallaCamara(
            alCapturarFoto = { uri ->
                viewModel.seleccionarImagen(uri)
                mostrarCamara = false
            },
            alSeleccionarGaleria = {
                lanzadorGaleria.launch("image/*")
            },
            alCerrar = {
                mostrarCamara = false
            }
        )
    } else {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Registrar animal", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = alCompletarRegistro) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color(0xFFF5F0FA))
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¡Comparte la información del animal para que reciba ayuda!",
                style = MaterialTheme.typography.bodyMedium,
                color = PurpleDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Zona de subida de foto ──
            ZonaSubirFoto(
                imagenUri = imagenUri,
                alAbrirCamara = { abrirCamara() },
                alEliminarImagen = { viewModel.seleccionarImagen(null) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Selección de tipo de animal ──
            EncabezadoSeccion(texto = "Tipo de animal")

            Spacer(modifier = Modifier.height(8.dp))

            SelectorTipoAnimal(
                tipoSeleccionado = tipoSeleccionado,
                alSeleccionarTipo = { tipoSeleccionado = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Información del animal ──
            EncabezadoSeccion(texto = "Información del animal")

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nombreAnimal,
                onValueChange = { nombreAnimal = it },
                label = { Text("Nombre (opcional)") },
                leadingIcon = { Icon(Icons.Outlined.Pets, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    unfocusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    focusedLabelColor = PurpleDark,
                    unfocusedLabelColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedLeadingIconColor = PurpleDark,
                    unfocusedLeadingIconColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedBorderColor = GradientStart,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF9B8FA8)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = raza,
                onValueChange = { raza = it },
                label = { Text("Raza (opcional)") },
                leadingIcon = { Icon(Icons.Outlined.Category, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    unfocusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    focusedLabelColor = PurpleDark,
                    unfocusedLabelColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedLeadingIconColor = PurpleDark,
                    unfocusedLeadingIconColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedBorderColor = GradientStart,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF9B8FA8)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (comportamiento, salud)") },
                leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = MaterialTheme.shapes.medium,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    unfocusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    focusedLabelColor = PurpleDark,
                    unfocusedLabelColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedLeadingIconColor = PurpleDark,
                    unfocusedLeadingIconColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedBorderColor = GradientStart,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF9B8FA8)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Ubicación y contacto ──
            EncabezadoSeccion(texto = "Ubicación y contacto")

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación del animal") },
                leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    unfocusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    focusedLabelColor = PurpleDark,
                    unfocusedLabelColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedLeadingIconColor = PurpleDark,
                    unfocusedLeadingIconColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedBorderColor = GradientStart,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF9B8FA8)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = contacto,
                onValueChange = { contacto = it },
                label = { Text("Teléfono o email de contacto") },
                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    unfocusedTextColor = androidx.compose.ui.graphics.Color(0xFF1D1A20),
                    focusedLabelColor = PurpleDark,
                    unfocusedLabelColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedLeadingIconColor = PurpleDark,
                    unfocusedLeadingIconColor = androidx.compose.ui.graphics.Color(0xFF49454F),
                    focusedBorderColor = GradientStart,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF9B8FA8)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Botón de enviar ──
            Button(
                onClick = {
                    viewModel.registrarAnimal(
                        nombre = nombreAnimal,
                        tipo = tipoSeleccionado,
                        raza = raza,
                        descripcion = descripcion,
                        ubicacion = ubicacion,
                        contacto = contacto
                    )
                },
                enabled = !estandoEnviando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GradientStart,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                if (estandoEnviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrando...",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar animal",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    } // fin else (mostrarCamara)
}

/**
 * Encabezado reutilizable para las secciones del formulario.
 */
@Composable
private fun EncabezadoSeccion(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = PurpleDark,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Zona de subida de foto funcional.
 * Si no hay imagen seleccionada, muestra el placeholder con ícono de cámara.
 * Si hay imagen seleccionada, muestra la vista previa con botón para eliminar.
 * Al tocar, abre la cámara integrada de la app.
 */
@Composable
private fun ZonaSubirFoto(
    imagenUri: Uri?,
    alAbrirCamara: () -> Unit,
    alEliminarImagen: () -> Unit
) {
    if (imagenUri != null) {
        // ── Vista previa de la imagen seleccionada ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                .clickable { alAbrirCamara() }
        ) {
            AsyncImage(
                model = imagenUri,
                contentDescription = "Foto seleccionada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Botón para eliminar la imagen
            SmallFloatingActionButton(
                onClick = alEliminarImagen,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Eliminar foto",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    } else {
        // ── Placeholder para seleccionar foto ──
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                .clickable { alAbrirCamara() },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = "Subir foto",
                    modifier = Modifier.size(40.dp),
                    tint = PurpleDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Toca para agregar una foto",
                    style = MaterialTheme.typography.bodySmall,
                    color = PurpleDark
                )
            }
        }
    }
}

/**
 * Selector basado en chips para elegir el tipo de animal.
 * Muestra opciones de Perro, Gato u Otro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorTipoAnimal(
    tipoSeleccionado: TipoAnimal,
    alSeleccionarTipo: (TipoAnimal) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // TipoAnimal.entries.forEach { tipo ->  // Oculto: muestra todos los tipos
        listOf(TipoAnimal.PERRO).forEach { tipo -> // Gato y Otro ocultos temporalmente
            val estaSeleccionado = tipo == tipoSeleccionado
            FilterChip(
                selected = estaSeleccionado,
                onClick = { alSeleccionarTipo(tipo) },
                label = {
                    Text(
                        text = tipo.etiqueta,
                        fontWeight = if (estaSeleccionado) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GradientStart.copy(alpha = 0.15f),
                    selectedLabelColor = PurpleDark,
                    labelColor = androidx.compose.ui.graphics.Color(0xFF1D1A20)
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaRegistroPreview() {
    HuellitasTheme {
        PantallaRegistroAnimal(alCompletarRegistro = {})
    }
}
