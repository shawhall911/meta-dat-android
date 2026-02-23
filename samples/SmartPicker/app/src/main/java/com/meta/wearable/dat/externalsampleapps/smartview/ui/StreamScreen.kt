// StreamScreen - SmartView Camera Streaming with AI Analysis UI
//
// This composable demonstrates the main streaming UI with AI scene understanding
// and audio feedback capabilities.

package com.meta.wearable.dat.externalsampleapps.smartview.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meta.wearable.dat.camera.types.StreamSessionState
import com.meta.wearable.dat.externalsampleapps.smartview.stream.StreamViewModel
import com.meta.wearable.dat.externalsampleapps.smartview.wearables.WearablesViewModel

@Composable
fun StreamScreen(
    wearablesViewModel: WearablesViewModel,
    modifier: Modifier = Modifier,
    streamViewModel: StreamViewModel =
        viewModel(
            factory =
                StreamViewModel.Factory(
                    application = (LocalActivity.current as ComponentActivity).application,
                    wearablesViewModel = wearablesViewModel,
                    aiServiceType = wearablesViewModel.uiState.collectAsStateWithLifecycle().value.aiServiceType,
                ),
        ),
) {
  val streamUiState = streamViewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) { streamViewModel.startStream() }

  Box(modifier = modifier.fillMaxSize()) {
    streamUiState.value.videoFrame?.let { videoFrame ->
      Image(
          bitmap = videoFrame.asImageBitmap(),
          contentDescription = "Live stream",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
      )
    }
    if (streamUiState.value.streamSessionState == StreamSessionState.STARTING) {
      CircularProgressIndicator(
          modifier = Modifier.align(Alignment.Center),
      )
    }

    // AI Analysis Card
    if (streamUiState.value.aiAnalysis != null || streamUiState.value.isAnalyzing) {
      Card(
          modifier =
              Modifier.align(Alignment.TopCenter)
                  .fillMaxWidth()
                  .padding(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
          shape = RoundedCornerShape(12.dp),
      ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            if (streamUiState.value.isSpeaking) {
              Icon(
                  imageVector = Icons.Default.VolumeUp,
                  contentDescription = "Speaking",
                  tint = AppColor.Green,
                  modifier = Modifier.width(20.dp).height(20.dp),
              )
            }
            Text(
                text = "AI Analysis",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            if (streamUiState.value.isAnalyzing) {
              Spacer(modifier = Modifier.width(8.dp))
              CircularProgressIndicator(
                  modifier = Modifier.width(16.dp).height(16.dp),
                  color = Color.White,
                  strokeWidth = 2.dp,
              )
            }
          }
          streamUiState.value.aiAnalysis?.let { analysis ->
            Text(
                text = analysis,
                color = Color.White,
                fontSize = 12.sp,
            )
          }
        }
      }
    }

    // Control buttons at bottom
    Box(modifier = Modifier.fillMaxSize().padding(all = 24.dp)) {
      Row(
          modifier =
              Modifier.align(Alignment.BottomCenter)
                  .navigationBarsPadding()
                  .fillMaxWidth()
                  .height(56.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        SwitchButton(
            label = if (streamUiState.value.analysisEnabled) "Disable AI" else "Enable AI",
            onClick = { streamViewModel.toggleAnalysis() },
            modifier = Modifier.weight(1f),
        )

        SwitchButton(
            label = "Stop Stream",
            onClick = {
              streamViewModel.stopStream()
              wearablesViewModel.navigateToDeviceSelection()
            },
            isDestructive = true,
            modifier = Modifier.weight(1f),
        )
      }
    }
  }
}
