// HomeScreen - DAT Registration Entry Point
//
// This screen handles DAT device registration.

package com.meta.wearable.dat.externalsampleapps.smartview.ui

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.wearable.dat.externalsampleapps.smartview.wearables.WearablesViewModel

@Composable
fun HomeScreen(
    viewModel: WearablesViewModel,
    modifier: Modifier = Modifier,
) {
  val scrollState = rememberScrollState()
  val activity = LocalActivity.current
  val context = LocalContext.current

  Box(modifier = modifier.fillMaxSize()) {
    // Settings button in top right
    SettingsButton(
        viewModel = viewModel,
        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
    )
    
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = 24.dp)
                .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      Spacer(modifier = Modifier.weight(1f))
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(
            text = "SmartView",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "AI-Powered Scene Understanding",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
      }
      Spacer(modifier = Modifier.weight(1f))
      Column(
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
      ) {
        Text(
            text = "Connect your Meta AI glasses to start analyzing scenes with AI and receive audio feedback.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
      }
      SwitchButton(
          label = "Connect my glasses",
          onClick = {
            activity?.let { viewModel.startRegistration(it) }
            Toast.makeText(context, "You'll be redirected to the Meta AI app", Toast.LENGTH_SHORT)
                .show()
          },
          modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.weight(1f))
    }
  }
}
