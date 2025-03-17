package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.ui.components.CheckContinueButton
import com.uw.duocode.ui.components.ProgressBar
import com.uw.duocode.ui.components.QuestionTopBar
import com.uw.duocode.ui.components.ResultBanner

// function to determine if a color is dark (for text contrast)
private fun isColorDark(color: Color): Boolean {
    // luminance - a value of 0.5 or higher is considered light
    return color.luminance() < 0.5
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchView(
    navController: NavHostController,
    matchViewModel: MatchViewModel = viewModel()
) {
    val questionText = matchViewModel.questionText
    val items = matchViewModel.items
    val selectedItem = matchViewModel.selectedItem
    val correctKeys = matchViewModel.correctKeys
    val correctValues = matchViewModel.correctValues
    val showErrorDialog = matchViewModel.showErrorDialog
    val answerChecked = matchViewModel.answerChecked
    val isAnswerCorrect = matchViewModel.isAnswerCorrect
    val progress = matchViewModel.progress

    Scaffold(
        topBar = {
            QuestionTopBar(
                navController = navController,
                progress = progress
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                CheckContinueButton(
                    text = if (!answerChecked) "CHECK" else "CONTINUE",
                    onClick = {
                        if (!answerChecked) {
                            matchViewModel.checkAnswer()
                        } else {
                            matchViewModel.continueToNext()
                        }
                    },
                    enabled = if (!answerChecked) matchViewModel.allMatchesMade else true,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                )

                if (answerChecked) {
                    ResultBanner(
                        isCorrect = isAnswerCorrect,
                        message = if (isAnswerCorrect) "Correct!" else "Incorrect!",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .zIndex(0f)
                    )
                } else if (showErrorDialog) {
                    ResultBanner(
                        isCorrect = false,
                        message = "Incorrect!",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .zIndex(0f)
                    )
                }
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
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 25.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    val isKey = index % 2 == 0
                    val isSelected = selectedItem?.index == item.index && selectedItem.isKey == isKey
                    val isMatched = (correctKeys.contains(item.index) && isKey) || (correctValues.contains(item.index) && !isKey)

                    val itemColor = matchViewModel.getItemColor(item.index, isKey)
                    
                    val containerColor = when {
                        itemColor != null -> itemColor
                        isSelected -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }
                    
                    val textColor = if (itemColor != null) {
                        if (isColorDark(itemColor)) Color.White else Color.Black
                    } else if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    OutlinedCard(
                        onClick = if (!isMatched) { { matchViewModel.onItemClicked(item, isKey) } } else { {} },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isMatched) itemColor ?: MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = containerColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.item,
                                fontSize = 16.sp,
                                color = textColor,
                                fontWeight = FontWeight.Medium,
                                maxLines = 3,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
