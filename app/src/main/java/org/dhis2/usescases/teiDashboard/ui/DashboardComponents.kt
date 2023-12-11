package org.dhis2.usescases.teiDashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EnrollmentProgramComponent() {
  Column {
      Text(
          text = "Testando o layout",
          modifier = Modifier.size(16.dp)
      )
  }
}