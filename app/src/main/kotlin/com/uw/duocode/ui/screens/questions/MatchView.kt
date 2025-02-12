package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.ui.components.ProgressBar
import com.uw.duocode.ui.components.ErrorBanner

fun matchViewOnContinue() { /* TODO */
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchView(
    navController: NavHostController,
    matchScreenViewModel: MatchScreenViewModel = viewModel()
) {
    val questionText = matchScreenViewModel.questionText
    val items = matchScreenViewModel.items
    val selectedItem = matchScreenViewModel.selectedItem
    val correctMatches = matchScreenViewModel.correctMatches
    val showErrorDialog = matchScreenViewModel.showErrorDialog
    val allMatchesCorrect = matchScreenViewModel.allMatchesCorrect

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        ProgressBar(
                            progress = 0.3f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { matchViewOnContinue() },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allMatchesCorrect) Color(0xFF6A4CAF) else Color.Gray,
                    contentColor = Color.White
                ),
                enabled = allMatchesCorrect
            ) {
                Text(text = "Continue", fontSize = 16.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = questionText,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 25.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    val isSelected = selectedItem == item
                    val isMatched = correctMatches.contains(item)
                    OutlinedCard(
                        onClick = { matchScreenViewModel.onItemClicked(item) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isMatched) Color(0xFF6A4CAF) else Color.Gray
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = when {
                                isMatched -> Color(0xFF6A4CAF)
                                isSelected -> Color(0xFFEDE7F6)
                                else -> Color.White
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(18.dp)
                                .height(50.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                color = if (isMatched) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }

        // error dialog
        if (showErrorDialog) {
            ErrorBanner(
                message = "❌ Incorrect! Try Again",
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
            )
        }
    }
}
