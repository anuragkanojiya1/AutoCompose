package com.example.autocompose.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.autocompose.R
import com.example.autocompose.auth.GoogleSignInUtils
import com.example.autocompose.data.datastore.PreferencesManager
import com.example.autocompose.ui.components.FontSelectionDialog
import com.example.autocompose.ui.components.LanguageSelectionDialog
import com.example.autocompose.ui.components.WritingStyleSelectionDialog
import com.example.autocompose.ui.navigation.Screen
import com.example.autocompose.ui.theme.AutoComposeTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val subscriptionTier by preferencesManager.subscriptionTierFlow.collectAsState(initial = "free")

    val primaryColor = Color(0xFF2196F3)
    val lightBgColor = Color(0xFFFEF6F2) // Light background color for profile section
    val scrollState = rememberScrollState()

    val user = FirebaseAuth.getInstance().currentUser
    val photoUrl = user?.photoUrl?.toString()

    val appDefaultFont by preferencesManager.fontFamilyFlow.collectAsState(initial = FontFamily.Default)
    val appDefaultWritingStyle by preferencesManager.writingStyleFlow.collectAsState(initial = "Formal")
    val appDefaultLanguage by preferencesManager.languageFlow.collectAsState(initial = "English")

    var showFontDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showWritingStyleDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Log.d("SettingsScreen", "photoUrl: $photoUrl")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Profile Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 8.dp),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Image
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Transparent)
                    ) {
                        // Replace with actual profile image if available
                       AsyncImage(
                           model = photoUrl,
                           contentDescription = "Profile Image",
                           contentScale = ContentScale.Crop,
                           modifier = Modifier.fillMaxSize()
                       )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = user?.displayName.toString(),
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.background
                        )
                        Text(
                            text = user?.email.toString(),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    IconButton(onClick = { /* Edit profile */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = primaryColor
                        )
                    }
                }
            }

            // Premium Section
            if (subscriptionTier == "premium") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Premium",
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "Active until Dec 2024",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { /* Manage subscription */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Manage Subscription")
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Manage",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            else{
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Free",
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "Access Basic features for free",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { navController.navigate(Screen.SubscriptionScreen.route) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Buy Premium")
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Manage",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }


            SectionHeader(text = "Writing Preferences")

            PreferenceItem(
                title = "Default Font",
                value = when (appDefaultFont) {
                    FontFamily.Serif -> "Serif"
                    FontFamily.SansSerif -> "Sans Serif"
                    FontFamily.Monospace -> "Monospace"
                    else -> "Default"
                },
                onClick = {
                    showFontDialog = true
                }
            )



            if (showFontDialog) {
                FontSelectionDialog(
                    currentFont = when (appDefaultFont) {
                        FontFamily.Serif -> "Serif"
                        FontFamily.SansSerif -> "SansSerif"
                        FontFamily.Monospace -> "Monospace"
                        else -> "Default"
                    },
                    onFontSelected = { fontFamily ->
                        coroutineScope.launch {
                            preferencesManager.saveFontFamily(fontFamily)
                            Log.d("Preferences", "Font changed to $fontFamily")
                            showFontDialog = false
                        }
                    },
                    onDismiss = { showFontDialog = false }
                )
            }


            PreferenceItem(
                title = "Writing Style",
                value = appDefaultWritingStyle,
                onClick = { showWritingStyleDialog = true }
            )

            if (showWritingStyleDialog) {
                WritingStyleSelectionDialog(
                    currentWritingStyle = appDefaultWritingStyle,
                    onWritingStyleSelected = { writingStyle ->
                        coroutineScope.launch {
                            preferencesManager.saveWritingStyle(writingStyle)
                            Log.d("Preferences", "Writing style changed to $writingStyle")
                            showWritingStyleDialog = false
                        }
                    },
                    onDismiss = { showWritingStyleDialog = false }
                )
            }


            PreferenceItem(
                title = "Language",
                value = appDefaultLanguage,
                onClick = { showLanguageDialog = true }
            )

            if (showLanguageDialog) {
                LanguageSelectionDialog(
                    currentLanguage = appDefaultLanguage,
                    onLanguageSelected = { language ->
                        coroutineScope.launch {
                            preferencesManager.saveLanguage(language)
                            Log.d("Preferences", "Language changed to $language")
                            showLanguageDialog = false
                        }
                    },
                    onDismiss = { showLanguageDialog = false }
                )
            }


            // Premium Features Section
            SectionHeader(text = "Premium Features")

            FeatureItem(
                title = "Advanced Grammar Check",
                isEnabled = true
            )

            FeatureItem(
                title = "Plagiarism Detection",
                isEnabled = true
            )

            FeatureItem(
                title = "AI Writing Assistant",
                isEnabled = true
            )

            FeatureItem(
                title = "Templates Library",
                isEnabled = true
            )

            FeatureItem(
                title = "Export to Multiple Formats",
                isEnabled = true
            )

            // Support Section
            SectionHeader(text = "Support")

            SupportItem(
                title = "Help Center",
                icon = Icons.Default.Help,
                onClick = { /* Open help center */ }
            )

            SupportItem(
                title = "Contact Support",
                icon = Icons.Default.Help,
                onClick = { /* Contact support */ }
            )

            SupportItem(
                title = "Privacy Policy",
                icon = Icons.Default.Lock,
                onClick = { /* View privacy policy */ }
            )

            SupportItem(
                title = "Terms of Service",
                icon = Icons.Default.Info,
                onClick = { /* View terms */ }
            )

            // Sign Out Button
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    GoogleSignInUtils.signOut(context)
                    navController.navigate(Screen.LogIn.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Sign Out")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    )
}

@Composable
fun PreferenceItem(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "●  "+title,
            fontSize = 16.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onClick()
            }
        ) {
            Text(
                text = value,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

//    Divider(
//        modifier = Modifier.padding(horizontal = 16.dp),
//        color = Color(0xFFEEEEEE)
//    )
}

@Composable
fun FeatureItem(title: String, isEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF2196F3),
            modifier = Modifier.padding(end = 16.dp)
        )

        Text(
            text = title,
            fontSize = 16.sp
        )
    }

//    Divider(
//        modifier = Modifier.padding(horizontal = 16.dp),
//        color = Color(0xFFEEEEEE)
//    )
}

@Composable
fun SupportItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(20.dp)
            )

            Text(
                text = title,
                fontSize = 16.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color.Gray
        )
    }

//    Divider(
//        modifier = Modifier.padding(horizontal = 16.dp),
//        color = Color(0xFFEEEEEE)
//    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    AutoComposeTheme {
        SettingsScreen(rememberNavController())
    }
}
