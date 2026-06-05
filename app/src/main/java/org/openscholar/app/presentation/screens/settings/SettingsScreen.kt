package org.openscholar.app.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(title = "AI Provider", description = "OpenRouter")
            Spacer(modifier = Modifier.height(8.dp))
            SettingsItem(title = "Default Model", description = "Qwen 2.5 72B")
            Spacer(modifier = Modifier.height(8.dp))
            SettingsItem(title = "Dark Mode", description = "System default", hasSwitch = true)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsItem(title = "Local Processing", description = "Use on-device AI when available", hasSwitch = true)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsItem(title = "Biometric Lock", description = "Secure app with fingerprint", hasSwitch = true)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsItem(title = "Export", description = "Backup your data")
            Spacer(modifier = Modifier.height(8.dp))
            SettingsItem(title = "About", description = "OpenScholar v1.0.0")
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    description: String,
    hasSwitch: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (hasSwitch) {
                Spacer(modifier = Modifier.height(8.dp))
                Switch(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
