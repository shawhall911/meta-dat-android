// NonStreamScreen - DAT Device Selection and Setup
//
// This screen demonstrates DAT device management and pre-streaming setup.

package com.meta.wearable.dat.externalsampleapps.smartpicker.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.meta.wearable.dat.externalsampleapps.smartpicker.wearables.WearablesViewModel

@Composable
fun NonStreamScreen(
    viewModel: WearablesViewModel,
    onRequestWearablesPermission: suspend (Permission) -> PermissionStatus,
    modifier: Modifier = Modifier,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Column(
      modifier =
          modifier
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
          text = "SmartPicker Ready",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
      )
      if (!uiState.hasActiveDevice) {
        CircularProgressIndicator()
        Text(
            text = "Waiting for an active device",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
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
        enabled = uiState.hasActiveDevice,
    )
    Spacer(modifier = Modifier.weight(1f))
  }
}
