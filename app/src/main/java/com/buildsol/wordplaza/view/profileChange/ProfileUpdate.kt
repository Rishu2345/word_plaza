package com.buildsol.wordplaza.view.profileChange

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.buildsol.wordplaza.R
import com.buildsol.wordplaza.viewModel.profileUpdateViewModel.ProfileUpdateViewModel
import com.buildsol.wordplaza.images.Image
import com.buildsol.wordplaza.images.imagePainter


@Composable
fun ProfileUpdate(
    viewModel: ProfileUpdateViewModel
){
    val uiState by viewModel.uiState.collectAsState()
    ProfileUpdate(
        state = uiState,
        onAvatarSelected = viewModel::onAvtarClicked,
        toggleAvtarSheet = viewModel::toggleAvatarSheet,
        onDisplayNameChange = viewModel::onDisplayNameChange,
        onSaveClick = viewModel::saveProfile,
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUpdate(
    state: ProfileUpdateState,
    onAvatarSelected: (Image) -> Unit,
    toggleAvtarSheet: (Boolean) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
) {



    if (state.showAvtarSheet) {
        ModalBottomSheet(onDismissRequest = { toggleAvtarSheet(false) } ) {
            ImageSourceSheet(
                selectedAvatarId = state.selectedAvatarId,
                avatars = state.avatars,
                onAvatarSelected = { avatar ->
                    onAvatarSelected(avatar.imageRes)
                    toggleAvtarSheet(false)
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            ProfileUpdateContent(
                state = state,
                onImageEditClick = { toggleAvtarSheet(true) },
                onNameChange = onDisplayNameChange,
                onSaveClick = onSaveClick
            )
        }
    }
}


@Composable
private fun ProfileUpdateContent(
    state: ProfileUpdateState,
    onImageEditClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        EditableAvatar(
            state = state,
            onImageEditClick = onImageEditClick
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.displayName,
                    onValueChange = onNameChange,
                    enabled = !state.isSaving,
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                state.successMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = onSaveClick,
                    enabled = !state.isSaving && state.displayName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text("Saving")
                    } else {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.size(10.dp))
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableAvatar(
    state: ProfileUpdateState,
    onImageEditClick: () -> Unit
) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if(state.selectedAvatarId != null) {
                    Image(
                        painter = imagePainter(state.selectedAvatarId),
                        contentDescription = "Selected Avtar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
            }else {
                AsyncImage(
                    model  = ImageRequest.Builder(LocalContext.current)
                        .data(state.profilePictureUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.onboarding_first),
                    error = painterResource(R.drawable.onboarding_first),
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        IconButton(
            onClick = onImageEditClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Change profile picture",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun ImageSourceSheet(
    selectedAvatarId: Image?,
    avatars: List<Avatar>,
    onAvatarSelected: (Avatar) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Choose Avatar",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = avatars,
                key = { it.id }
            ) { avatar ->

                val isSelected = avatar.imageRes == selectedAvatarId

                AvatarItem(
                    avatar = avatar,
                    isSelected = isSelected,
                    onClick = {
                        onAvatarSelected(avatar)
                    }
                )
            }
        }
    }
}

@Composable
private fun AvatarItem(
    avatar: Avatar,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {

        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = imagePainter( avatar.imageRes),
                contentDescription = avatar.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = avatar.title,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

data class Avatar(
    val id: Int,
    val imageRes: Image,
    val title: String
)