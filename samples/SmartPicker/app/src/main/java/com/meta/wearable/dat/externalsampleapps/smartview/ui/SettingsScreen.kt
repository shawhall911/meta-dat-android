// SettingsScreen - SmartView Settings UI
//
// Allows users to configure AI service preferences

package com.meta.wearable.dat.externalsampleapps.smartview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meta.wearable.dat.externalsampleapps.smartview.settings.AiServiceType
import com.meta.wearable.dat.externalsampleapps.smartview.wearables.WearablesViewModel

@Composable
fun SettingsButton(
    viewModel: WearablesViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    
    IconButton(
        onClick = { viewModel.showSettings() },
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
        )
    }
    
    if (uiState.showSettings) {
        SettingsDialog(
            currentType = uiState.aiServiceType,
            onDismiss = { viewModel.hideSettings() },
            onSelect = { type -> 
                viewModel.setAiServiceType(type)
                viewModel.hideSettings()
            },
        )
    }
}

@Composable
fun SettingsDialog(
    currentType: AiServiceType,
    onDismiss: () -> Unit,
    onSelect: (AiServiceType) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI Service Settings") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Choose AI service for scene analysis:",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = currentType == AiServiceType.HUGGING_FACE,
                        onClick = { onSelect(AiServiceType.HUGGING_FACE) },
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text("Hugging Face API", style = MaterialTheme.typography.bodyLarge)
                        Text("Real AI analysis (requires internet)", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = currentType == AiServiceType.MOCK,
                        onClick = { onSelect(AiServiceType.MOCK) },
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text("Mock Service", style = MaterialTheme.typography.bodyLarge)
                        Text("Offline mode for testing", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}
