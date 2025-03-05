package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.ui.components.CheckContinueButton
import com.uw.duocode.ui.components.ProgressBar
import com.uw.duocode.ui.components.ResultBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchView(
    navController: NavHostController,
    matchViewModel: MatchViewModel = viewModel()
) {
    val questionText = matchViewModel.questionText
    val items = matchViewModel.items
    val shuffledKeys = matchViewModel.shuffledKeys
//    val shuffleValues = matchViewModel.shuffledValues
    val selectedItem = matchViewModel.selectedItem
//    val correctMatches = matchViewModel.correctMatches
    val correctKeys = matchViewModel.correctKeys
    val correctValues = matchViewModel.correctValues
    val showErrorDialog = matchViewModel.showErrorDialog
    val allMatchesCorrect = matchViewModel.allMatchesCorrect
    val progress = matchViewModel.progress

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
                            progress = progress,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                CheckContinueButton(
                    text = "Continue",
                    onClick = { matchViewModel.continueToNext() },
                    enabled = allMatchesCorrect,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                )
                if (showErrorDialog) {
                    ResultBanner(
                        isCorrect = false,
                        message = "Incorrect! Try Again",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .zIndex(0f)
                    )
                } else if (allMatchesCorrect) {
                    ResultBanner(
                        isCorrect = true,
                        message = "Correct!",
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
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                itemsIndexed(items) { index, item ->
                    val isKey = index % 2 == 0
                    val isSelected = selectedItem?.index == item.index && selectedItem.isKey == isKey
                    val isMatched = (correctKeys.contains(item.index) && isKey) || (correctValues.contains(item.index) && !isKey)

                    val containerColor = when {
                        isMatched -> {
                            val pairList = matchViewModel.correctPairs.entries.toList()
                            val pairIndex = pairList.indexOfFirst { (k, v) ->
                                k == item.item || v == item.item
                            }
                            val alpha = if (pairIndex >= 0) 1f - 0.25f * pairIndex else 1f
                            MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                        }

                        isSelected -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }

                    OutlinedCard(
                        onClick = if (!isMatched) { { matchViewModel.onItemClicked(item, isKey) } } else { {} },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isMatched) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        colors = androidx.compose.material3.CardDefaults.outlinedCardColors(
                            containerColor = containerColor
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
                                text = item.item,
                                fontSize = 16.sp,
                                color = if (isMatched) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
