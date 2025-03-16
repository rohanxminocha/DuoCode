package com.uw.duocode.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionTopBar(
    navController: NavHostController,
    progress: Float
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    
    if (showConfirmationDialog) {
        ConfirmationDialog(
            title = "Leave Question?",
            message = "If you leave now, your progress on this question will be lost. Are you sure you want to go back?",
            onConfirm = { 
                navController.popBackStack() 
            },
            onDismiss = { 
                showConfirmationDialog = false 
            },
            confirmText = "Leave",
            dismissText = "Stay"
        )
    }
    
    SmallTopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { showConfirmationDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                ProgressBar(
                    progress = progress,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    )
} 