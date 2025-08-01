package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.ui.components.CheckContinueButton
import com.uw.duocode.ui.components.QuestionTopBar
import com.uw.duocode.ui.components.ResultBanner


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoiceView(
    navController: NavHostController,
    viewModel: MultipleChoiceViewModel = viewModel()
) {
    val questionText = viewModel.questionText
    val options = viewModel.options
    val selectedOption = viewModel.selectedOption
    val answerChecked = viewModel.answerChecked
    val isAnswerCorrect = viewModel.isAnswerCorrect
    val progress = viewModel.progress

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
                            viewModel.checkAnswer()
                        } else {
                            viewModel.continueToNext()
                        }
                    },
                    enabled = if (!answerChecked) selectedOption != null else true,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                )

                if (answerChecked) {
                    ResultBanner(
                        isCorrect = isAnswerCorrect,
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

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(options) { _, option ->
                    val isSelected = (option == selectedOption)
                    val borderColor = when {
                        !answerChecked && isSelected -> MaterialTheme.colorScheme.primary
                        answerChecked && isSelected && viewModel.isAnswerCorrect -> MaterialTheme.colorScheme.tertiary
                        answerChecked && isSelected && !viewModel.isAnswerCorrect -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.outline
                    }

                    OutlinedCard(
                        onClick = { viewModel.onOptionSelected(option) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, borderColor),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor =
                            if (isSelected && !answerChecked) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !answerChecked) {
                                viewModel.onOptionSelected(option)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(18.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
