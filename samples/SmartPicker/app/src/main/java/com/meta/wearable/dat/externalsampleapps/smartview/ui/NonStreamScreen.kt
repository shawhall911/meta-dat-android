// NonStreamScreen - DAT Device Selection and Setup
//
// This screen demonstrates DAT device management and pre-streaming setup.

package com.meta.wearable.dat.externalsampleapps.smartview.ui

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meta.wearable.dat.core.types.Permission
import com.meta.wearable.dat.core.types.PermissionStatus
import com.meta.wearable.dat.externalsampleapps.smartview.wearables.WearablesViewModel

@Composable
fun NonStreamScreen(
    viewModel: WearablesViewModel,
    onRequestWearablesPermission: suspend (Permission) -> PermissionStatus,
    modifier: Modifier = Modifier,
) {
  val uiState = viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  
  android.util.Log.d("NonStreamScreen", "Recomposing, showSettings = ${uiState.value.showSettings}")

  Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Top bar with back button and settings
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = { viewModel.navigateToHome() }) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
          )
        }
        
        // Show current AI service
        val aiType = uiState.value.aiServiceType
        Text(
            text = if (aiType.name == "HUGGING_FACE") "HF AI" else "Mock AI",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        
        IconButton(onClick = { 
          Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
          viewModel.showSettings() 
        }) {
          Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Settings",
          )
        }
      }
      
      // Main content
      Column(
          modifier =
              Modifier
                  .fillMaxSize()
                  .padding(all = 24.dp)
                  .navigationBarsPadding(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(24.dp),
      ) {
        Spacer(modifier = Modifier.weight(1f))
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
            text = "SmartView Ready",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        if (!uiState.value.hasActiveDevice) {
          CircularProgressIndicator()
          Text(
              text = "Waiting for an active device",
              fontSize = 14.sp,
              textAlign = TextAlign.Center,
          )
          // Cancel button when waiting for device
          SwitchButton(
              label = "Cancel",
              onClick = { viewModel.navigateToHome() },
              modifier = Modifier.fillMaxWidth(0.5f),
              isDestructive = true,
          )
        } else {
          Text(
              text = "Device connected. Tap below to start AI analysis.",
              fontSize = 14.sp,
              textAlign = TextAlign.Center,
          )
        }
      }
        Spacer(modifier = Modifier.weight(1f))
        SwitchButton(
            label = "Start AI Analysis",
            onClick = { viewModel.navigateToStreaming(onRequestWearablesPermission) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.value.hasActiveDevice,
        )
        Spacer(modifier = Modifier.weight(1f))
      }
    }
    
    // Settings dialog - outside the Column, on top of everything
    if (uiState.value.showSettings) {
      SettingsDialog(
          currentType = uiState.value.aiServiceType,
          onDismiss = { viewModel.hideSettings() },
          onSelect = { type -> 
            viewModel.setAiServiceType(type)
            viewModel.hideSettings()
          },
      )
    }
  }
}
