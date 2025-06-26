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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.autocompose.R
import com.example.autocompose.data.database.Entity
import com.example.autocompose.data.datastore.PreferencesManager
import com.example.autocompose.ui.navigation.Screen
import com.example.autocompose.ui.navigation.navigateToPayment
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
    val searchResults by frequentEmailViewModel.searchResults.collectAsState()
    val isSearchActive by frequentEmailViewModel.isSearchActive.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    val maxInitialEmails = 3
    val maxEmailsIncrement = 3
    val (displayCount, setDisplayCount) = remember { mutableStateOf(maxInitialEmails) }

    val context = LocalContext.current

    val preferencesManager = PreferencesManager(context)

    val subscriptionTier by preferencesManager.subscriptionTierFlow.collectAsState(initial = "free")

    // Add logging when emails are collected
    androidx.compose.runtime.LaunchedEffect(frequentEmails) {
        Log.d("HomeScreen", "Received ${frequentEmails.size} frequent emails")
        frequentEmails.forEachIndexed { index, email ->
            Log.d("HomeScreen", "Email $index: '${email.subject}' (used ${email.frequency} times)")
        }
    }

    // Handle searching
    fun handleSearch(query: String) {
        searchQuery = query
        if (query.isBlank()) {
            frequentEmailViewModel.clearSearch()
        } else {
            frequentEmailViewModel.searchEmails(query)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (showSearchBar) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { handleSearch(it) },
                                placeholder = { Text("Search emails...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 16.dp)
                                    .align(Alignment.CenterHorizontally),
                                textStyle = TextStyle(
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Default,
                                ),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        showSearchBar = false
                                        searchQuery = ""
                                        frequentEmailViewModel.clearSearch()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Close Search"
                                        )
                                    }
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    unfocusedTextColor = Color.Gray,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedBorderColor = Color.DarkGray,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer                        ),
                                shape = RoundedCornerShape(16.dp),
//                                colors = TextFieldDefaults.outlinedTextFieldColors(
//                                    unfocusedTextColor = Color.Gray,
//                                    unfocusedBorderColor = Color(0xFFE7E6E6),
//                                    focusedBorderColor = Color(0xFFE7E6E6)
//                                ),
//                                shape = RoundedCornerShape(12.dp),
                                )
                        } else {
                            if(subscriptionTier=="premium"){
                                Text("AutoCompose ✨")
                            } else{
                                Text("AutoCompose")
                            }
                        }
                    },
                    actions = {
                        if (!showSearchBar) {
                            IconButton(onClick = {
                                showSearchBar = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(bottom = 12.dp),
                    thickness = 0.65.dp,
                    color = Color(0xFFDCDBDB)
                )
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { navController.navigate(Screen.Home.route) },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .size(64.dp),
                            ) {
                                Icon(Icons.Default.Home, contentDescription = "Home",
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(32.dp))

                            IconButton(onClick = { navController.navigate(Screen.Analytics.route) },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .size(64.dp)
                            ) {
                                Icon(Icons.Default.TrendingUp, contentDescription = "Trends",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            )
        },
        modifier = Modifier.background(color = Color.White)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (!isSearchActive) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .background(color = MaterialTheme.colorScheme.background),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background)
                    ) {
                        Animation(
                            modifier = Modifier
                                .size(240.dp, 240.dp)
                                .align(Alignment.Center)
                                .background(MaterialTheme.colorScheme.background)
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

                // PayPal Payment Button
//                Button(
//                    onClick = { navController.navigateToPayment("10.00") },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF0070BA) // PayPal blue
//                    )
//                ) {
//                    Text(
//                        text = "Pay with PayPal",
//                        color = Color.White
//                    )
//                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Title Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSearchActive) "Search Results" else "Recent Emails",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                if (!isSearchActive && frequentEmails.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            frequentEmailViewModel.clearAllFrequentEmails()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete all emails",
                            tint = primaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Items - Show either search results or frequent emails
            val emailsToDisplay = if (isSearchActive) searchResults else frequentEmails

            if (emailsToDisplay.isNotEmpty()) {
                if (isSearchActive) {
                    Log.d("HomeScreen", "Displaying ${emailsToDisplay.size} search results")
                } else {
                    Log.d("HomeScreen", "Displaying ${emailsToDisplay.size} frequent emails in UI")
                }

                val displayLimit = if (isSearchActive) emailsToDisplay.size else displayCount.coerceAtMost(emailsToDisplay.size)

                emailsToDisplay.take(displayLimit).forEach { email ->
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
                if (isSearchActive) {
                    Log.d("HomeScreen", "No search results found")
                    Text(text = "No results found for '$searchQuery'")
                } else {
                    Log.d("HomeScreen", "No frequent emails found, showing static data")
                    Text(text = "No emails found")
                }
            }

            // Show More Button - only display when not searching
            if (!isSearchActive && frequentEmails.size > displayCount) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        setDisplayCount(displayCount + maxEmailsIncrement)
                    }
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
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Avatar with initials
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .align(Alignment.Top)
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = subject,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

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