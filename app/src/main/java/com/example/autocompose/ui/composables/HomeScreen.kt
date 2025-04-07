package com.example.autocompose.ui.composables

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.autocompose.ui.viewmodel.FrequentEmailViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.autocompose.R
import com.example.autocompose.data.database.Entity
import com.example.autocompose.ui.navigation.Screen
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    frequentEmailViewModel: FrequentEmailViewModel,
    application: Application,
    navController: NavController
) {
    val primaryBlue = Color(0xFF2196F3)

    val frequentEmails by frequentEmailViewModel.frequentEmails.collectAsState()

    val context = LocalContext.current

    // Add logging when emails are collected
    androidx.compose.runtime.LaunchedEffect(frequentEmails) {
        Log.d("HomeScreen", "Received ${frequentEmails.size} frequent emails")
        frequentEmails.forEachIndexed { index, email ->
            Log.d("HomeScreen", "Email $index: '${email.subject}' (used ${email.frequency} times)")
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("AutoCompose") },
                    actions = {
                        IconButton(onClick = {

                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
                Divider(
                    modifier = Modifier.padding(bottom = 12.dp),
                    thickness = 1.dp,
                    color = Color(0xFFDCDBDB)
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color.White),
                color = MaterialTheme.colorScheme.background
            ) {
                Box {
                    Animation(
                        modifier = Modifier
                        .size(240.dp, 240.dp)
                        .align(Alignment.Center)
                        .background(Color.White)
                            .padding(bottom = 8.dp)
                        // .scale(scaleX = 1.3f, scaleY = 1.6f)
                    )
                }
            }

            // New Email Button
            Button(
                onClick = { navController.navigate(Screen.AgentScreen.route) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Compose",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Emails Section
            Text(
                text = "Recent Emails",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Items - Show frequent emails from database
            if (frequentEmails.isNotEmpty()) {
                Log.d("HomeScreen", "Displaying ${frequentEmails.size} frequent emails in UI")
                frequentEmails.take(3).forEach { email ->
                    // Extract first letters from subject words to create initials
                    val words = email.subject.split(" ")
                    val initials = if (words.size >= 2) {
                        (words[0].firstOrNull()?.toString() ?: "") + (words[1].firstOrNull()?.toString() ?: "")
                    } else if (words.isNotEmpty()) {
                        words[0].take(2)
                    } else "EM"

                    EmailItem(
                        initials = initials.uppercase(),
                        name = email.subject,  // Using subject as name since we don't have sender info
                        subject = email.subject,
                        preview = email.emailBody.take(50) + if (email.emailBody.length > 50) "..." else "",
                        time = "Used ${email.frequency} times",
                        backgroundColor = Color(0xFFF8F7F7),
                        onClick = {
                            navController.navigate(
                                "draft_agent_screen/${Uri.encode(email.subject)}/${Uri.encode(email.emailBody)}"
                            )
                        }

                    )

                    Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))
                }
            } else {
                // Fallback to static data if no frequent emails
                Log.d("HomeScreen", "No frequent emails found, showing static data")
                EmailItem(
                    initials = "JS",
                    name = "John Smith",
                    subject = "Project Update",
                    preview = "Here are the latest changes to the project",
                    time = "10:30 AM",
                    backgroundColor = Color(0xFFF8F7F7),
                    onClick = {}
                )

                Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))

                EmailItem(
                    initials = "MT",
                    name = "Marketing Team",
                    subject = "Campaign Results",
                    preview = "The Q1 campaign metrics show significant",
                    time = "9:15 AM",
                    backgroundColor = Color(0xFFF8F7F7),
                    onClick = {}
                )

                Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))

                EmailItem(
                    initials = "SJ",
                    name = "Sarah Johnson",
                    subject = "Meeting Notes",
                    preview = "Please find attached the minutes from",
                    time = "Yesterday",
                    backgroundColor = Color(0xFFF8F7F7),
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show More Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F7F7))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Show More ➤",
                        color = primaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EmailItem(
    initials: String,
    name: String,
    subject: String,
    preview: String,
    time: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    val primaryBlue = Color(0xFF2196F3)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Avatar with initials
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = primaryBlue
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Text(
                text = subject,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            Text(
                text = preview,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        frequentEmailViewModel = FrequentEmailViewModel(Application()),
        application = Application(),
        navController = rememberNavController()
    )
}

@Composable
fun Animation(modifier: Modifier = Modifier) {
    val composition = rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.animation2)
    )
    
    val progress = animateLottieCompositionAsState(
        composition = composition.value,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    LottieAnimation(
        composition = composition.value,
        progress = { progress.value },
        modifier = modifier
    )
}