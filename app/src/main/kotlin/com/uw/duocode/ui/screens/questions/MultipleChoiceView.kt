package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
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
import com.uw.duocode.ui.components.CheckContinueButton
import com.uw.duocode.ui.components.ProgressBar
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
                    text = if (!answerChecked) "Check" else "Continue",
                    onClick = {
                        if (!answerChecked) {
                            viewModel.checkAnswer()
                        } else {
                            viewModel.continueToNext()
                        }
                    },
                    enabled = if (!answerChecked) selectedOption != null else true,
                    containerColor = Color(0xFF6A4CAF),
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 25.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(options) { }
                itemsIndexed(options) { index, option ->
                    val isSelected = selectedOption == option
                    val borderColor = if (!answerChecked) {
                        if (isSelected) Color(0xFF6A4CAF) else Color.Gray
                    } else {
                        val isCorrect = viewModel.correctAnswer.find { a -> a == index } != null
                        when {
                            isCorrect -> Color(0xFF4CAF50)
                            isSelected && !isCorrect -> Color(0xFFD32F2F)
                            else -> Color.Gray
                        }
                    }


                    OutlinedCard(
                        onClick = { viewModel.onOptionSelected(option) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, borderColor),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) Color(0xFFEDE7F6) else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !answerChecked) { viewModel.onOptionSelected(option) }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(18.dp)
                                .height(50.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
