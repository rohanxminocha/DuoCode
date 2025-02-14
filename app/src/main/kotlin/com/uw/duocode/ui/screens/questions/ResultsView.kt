package com.uw.duocode.ui.screens.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uw.duocode.R
import com.uw.duocode.ui.components.CheckContinueButton
import com.uw.duocode.ui.components.ProgressBar
import com.uw.duocode.ui.components.ResultBanner
import com.uw.duocode.ui.navigation.Home
import sh.calvin.reorderable.ReorderableColumn


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultView(
    navController: NavHostController
) {

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
                            progress = 1f,
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
                    onClick = {
                            navController.navigate(Home)
                    },
                    enabled = true,
                    containerColor = Color(0xFF6A4CAF),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                )
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
                text = "Congratulations \uD83C\uDF89 \n",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 25.dp)
            )
            Text(
                text = "You got all the questions correct!",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 25.dp)
            )
            Image(painter = painterResource(R.drawable.great),
                "great",
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 50.dp),
                contentScale = ContentScale.FillWidth
                )
        }
    }
}