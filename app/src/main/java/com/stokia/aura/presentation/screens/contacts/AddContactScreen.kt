@file:Suppress("DEPRECATION")

package com.stokia.aura.presentation.screens.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.stokia.aura.domain.model.AuraResult
import com.stokia.aura.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    navController: NavController,
    viewModel: ContactViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Buscar", "Mi Código", "Escanear")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Añadir Contacto",
                        fontWeight = FontWeight.Bold,
                        color = AuraTextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver",
                            tint = AuraTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraDeepBlack,
                    titleContentColor = AuraTextPrimary,
                    navigationIconContentColor = AuraTextPrimary
                )
            )
        },
        containerColor = AuraBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = AuraDeepBlack,
                contentColor = AuraCyan,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                title,
                                color = if (selectedTabIndex == index) AuraCyan else AuraTextMuted,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(AuraDeepBlack, AuraBlack)
                        )
                    )
            ) {
                when (selectedTabIndex) {
                    0 -> SearchContactTab(viewModel)
                    1 -> MyQrCodeTab(viewModel)
                    2 -> ScanQrCodeTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun SearchContactTab(viewModel: ContactViewModel) {
    var usernameQuery by remember { mutableStateOf("") }
    val searchResult by viewModel.searchResult.collectAsState()
    val addResult by viewModel.addContactResult.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val isAdding by viewModel.isAddingContact.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Busca por @usuario",
            color = AuraTextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Glassmorphism Search Input
        OutlinedTextField(
            value = usernameQuery,
            onValueChange = { usernameQuery = it.replace(" ", "").lowercase() },
            placeholder = { Text("username", color = AuraTextMuted) },
            leadingIcon = { Text("@", color = AuraCyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp)) },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(color = AuraCyan, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    IconButton(onClick = { 
                        focusManager.clearFocus()
                        if (usernameQuery.isNotBlank()) viewModel.searchUserByUsername(usernameQuery) 
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = AuraCyan)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuraCyan,
                unfocusedBorderColor = AuraGlassBorder,
                focusedContainerColor = AuraCardSurface.copy(alpha = 0.5f),
                unfocusedContainerColor = AuraCardSurface.copy(alpha = 0.5f),
                focusedTextColor = AuraTextPrimary,
                unfocusedTextColor = AuraTextPrimary
            ),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = {
                focusManager.clearFocus()
                if (usernameQuery.isNotBlank()) viewModel.searchUserByUsername(usernameQuery)
            })
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Result Card
        when (val res = searchResult) {
            is AuraResult.Failure -> {
                Text(
                    text = "Usuario no encontrado",
                    color = AuraError,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            is AuraResult.Success -> {
                val user = res.data
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(listOf(AuraCyanGlow, Color.Transparent)),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = AuraCardSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar placeholder for now
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(AuraDarkSurface)
                                .border(2.dp, AuraCyan, RoundedCornerShape(40.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(user.displayName.take(1).uppercase(), color = AuraCyan, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(user.displayName, color = AuraTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("@${user.username}", color = AuraCyan, fontSize = 14.sp)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { viewModel.addContactByUid(user.uid) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraCyan,
                                contentColor = AuraBlack
                            ),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isAdding
                        ) {
                            if (isAdding) {
                                CircularProgressIndicator(color = AuraBlack, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else if (addResult is AuraResult.Success) {
                                Text("¡Añadido!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            } else {
                                Text("Añadir Contacto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
            null -> {}
        }
    }
}

@Composable
fun MyQrCodeTab(viewModel: ContactViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (currentUser == null) {
            CircularProgressIndicator(color = AuraCyan)
            return@Box
        }

        val qrContent = "aura://contact/${currentUser?.username}"
        val qrBitmap = remember(qrContent) { QrCodeUtils.generateQrCode(qrContent, size = 600) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Neon glowing card
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(AuraCardSurface)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(AuraCyan, AuraViolet)),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(32.dp)
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = "Mi Código QR",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                "Escanea para añadir a",
                color = AuraTextSecondary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "@${currentUser?.username}",
                color = AuraTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ScanQrCodeTab(viewModel: ContactViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var hasCameraPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    ) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasCameraPermission = isGranted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Camera Preview with Rounded Corners
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
                    .border(2.dp, AuraGlassBorder, RoundedCornerShape(32.dp))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setTargetResolution(Size(1280, 720))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            val scanner = BarcodeScanning.getClient()

                            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                    scanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            for (barcode in barcodes) {
                                                if (barcode.valueType == Barcode.TYPE_URL || barcode.valueType == Barcode.TYPE_TEXT) {
                                                    val rawValue = barcode.rawValue
                                                    if (rawValue != null && rawValue.startsWith("aura://contact/")) {
                                                        val username = rawValue.removePrefix("aura://contact/")
                                                        viewModel.searchUserByUsername(username)
                                                    }
                                                }
                                            }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        
                        previewView
                    }
                )
                
                // Cyberpunk Scanner Overlay Box
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .align(Alignment.Center)
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(listOf(AuraCyan, AuraViolet, AuraCyan)),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(AuraCyanGlow.copy(alpha = 0.1f))
                )
            }

            // Results Card Overlay
            val searchResult by viewModel.searchResult.collectAsState()
            val addResult by viewModel.addContactResult.collectAsState()

            if (searchResult is AuraResult.Success) {
                val user = (searchResult as AuraResult.Success).data
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AuraCardSurface.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp), 
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¡Contacto Detectado!",
                            color = AuraCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "@${user.username}",
                            color = AuraTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Button(
                            onClick = { viewModel.addContactByUid(user.uid) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (addResult is AuraResult.Success) AuraGreen else AuraCyan,
                                contentColor = AuraBlack
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (addResult is AuraResult.Success) {
                                Text("Añadido con éxito", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            } else {
                                Text("Añadir a Contactos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Se necesita acceso a la cámara",
                    color = AuraTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraCyan, contentColor = AuraBlack)
                ) {
                    Text("Conceder Permiso")
                }
            }
        }
    }
}
