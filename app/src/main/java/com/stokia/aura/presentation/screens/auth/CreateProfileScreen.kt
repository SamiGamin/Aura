@file:Suppress("DEPRECATION")
package com.stokia.aura.presentation.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.stokia.aura.presentation.navigation.Routes
import com.stokia.aura.ui.theme.AuraBlack
import com.stokia.aura.ui.theme.AuraCardSurface
import com.stokia.aura.ui.theme.AuraCyan
import com.stokia.aura.ui.theme.AuraError
import com.stokia.aura.ui.theme.AuraGlassBorder
import com.stokia.aura.ui.theme.AuraTextMuted
import com.stokia.aura.ui.theme.AuraTextSecondary
import com.stokia.aura.ui.theme.AuraViolet
import kotlinx.coroutines.delay

@Suppress("DEPRECATION")
@Composable
fun CreateProfileScreen(
    navController: NavController,
    viewModel: CreateProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val username by viewModel.username.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    val localAvatarUri by viewModel.localAvatarUri.collectAsState()
    val localCoverUri by viewModel.localCoverUri.collectAsState()

    val focusManager = LocalFocusManager.current
    var showContent by remember { mutableStateOf(false) }

    // Launcher for Avatar (1:1 Ratio, Oval Crop)
    val avatarCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.onAvatarSelected(result.uriContent)
        }
    }

    // Launcher for Cover Image (16:9 Ratio, Rectangle Crop)
    val coverCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.onCoverSelected(result.uriContent)
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            navController.navigate(Routes.ChatList.route) {
                popUpTo(Routes.CreateProfile.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBlack)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AuraCyan.copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.15f),
                        radius = size.minDimension * 0.5f
                    ),
                    radius = size.minDimension * 0.5f,
                    center = Offset(size.width * 0.8f, size.height * 0.15f)
                )
            }
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(spring()) + slideInVertically(spring()) { it / 4 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                
                // --- PREMIUM HEADER (COVER + AVATAR) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    // Cover Image Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(AuraCardSurface)
                            .clickable {
                                coverCropLauncher.launch(
                                    CropImageContractOptions(
                                        uri = null,
                                        cropImageOptions = CropImageOptions(
                                            imageSourceIncludeGallery = true,
                                            imageSourceIncludeCamera = true,
                                            fixAspectRatio = true,
                                            aspectRatioX = 16,
                                            aspectRatioY = 9
                                        )
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (localCoverUri != null) {
                            AsyncImage(
                                model = localCoverUri,
                                contentDescription = "Portada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Añadir Portada",
                                    tint = AuraTextMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Toca para añadir portada", color = AuraTextMuted, fontSize = 12.sp)
                            }
                        }
                    }

                    // Avatar Overlapping the Cover Image
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(120.dp)
                            .offset(y = 10.dp) // Push it down to overlap
                            .clip(CircleShape)
                            .background(AuraBlack) // Background to simulate border gap
                            .border(
                                width = 3.dp,
                                brush = Brush.linearGradient(listOf(AuraCyan, AuraViolet)),
                                shape = CircleShape
                            )
                            .clickable {
                                avatarCropLauncher.launch(
                                    CropImageContractOptions(
                                        uri = null,
                                        cropImageOptions = CropImageOptions(
                                            imageSourceIncludeGallery = true,
                                            imageSourceIncludeCamera = true,
                                            cropShape = CropImageView.CropShape.OVAL,
                                            fixAspectRatio = true,
                                            aspectRatioX = 1,
                                            aspectRatioY = 1
                                        )
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (localAvatarUri != null) {
                            AsyncImage(
                                model = localAvatarUri,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Elegir foto",
                                tint = AuraTextMuted,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // --- FORM SECTION ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp)
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Configura tu perfil",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = AuraTextSecondary,
                            fontWeight = FontWeight.Light
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(AuraCardSurface.copy(alpha = 0.6f))
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(listOf(AuraGlassBorder, Color.Transparent)),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AuraTextField(
                            value = displayName,
                            onValueChange = { viewModel.onDisplayNameChange(it) },
                            label = "Nombre a mostrar",
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = AuraTextMuted) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AuraTextField(
                            value = username,
                            onValueChange = { viewModel.onUsernameChange(it) },
                            label = "Nombre de usuario único (@)",
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, null, tint = AuraTextMuted) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                viewModel.saveProfile()
                            })
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (uiState is ProfileUiState.Error) {
                            Text(
                                text = (uiState as ProfileUiState.Error).message,
                                style = MaterialTheme.typography.bodySmall.copy(color = AuraError),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (uiState is ProfileUiState.Saving) {
                            Text(
                                text = (uiState as ProfileUiState.Saving).message,
                                style = MaterialTheme.typography.bodySmall.copy(color = AuraCyan),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        AuraPrimaryButton(
                            text = "COMPLETAR PERFIL",
                            isLoading = uiState is ProfileUiState.Loading || uiState is ProfileUiState.Saving,
                            onClick = { viewModel.saveProfile() }
                        )
                    }
                }
            }
        }
    }
}
