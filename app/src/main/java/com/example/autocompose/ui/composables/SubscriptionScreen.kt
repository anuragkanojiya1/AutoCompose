package com.example.autocompose.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.autocompose.data.datastore.PreferencesManager
import com.example.autocompose.ui.navigation.Screen
import com.example.autocompose.ui.theme.AutoComposeTheme
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(navController: NavController, autoComposeViewmodel: AutoComposeViewmodel) {
    val scrollState = rememberScrollState()

    val context = LocalContext.current

    var token by rememberSaveable { mutableStateOf("") }

    val preferencesManager = PreferencesManager(context)

    val subscriptionState = autoComposeViewmodel.checkSubscription.collectAsState()

    val subscriptionTier by preferencesManager.subscriptionTierFlow.collectAsState(initial = "free")

    Log.d("SubscriptionTier", subscriptionTier)

    val subscription = subscriptionState.value

    val primary = Color(0xFF2196F3)

    if (token.isNotBlank()) {
        LaunchedEffect(Unit) {
            autoComposeViewmodel.checkSubscription(token)
            Log.d("Subscription", subscription.toString())
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if(subscriptionTier=="premium"){
                            Text("AutoCompose ✨")
                        } else{
                            Text("AutoCompose")
                        } },
//                    actions = {
//                        IconButton(onClick = { /* Settings action */ }) {
//                            Icon(
//                                imageVector = Icons.Default.Settings,
//                                contentDescription = "Settings"
//                            )
//                        }
//                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp),
                    thickness = 0.65.dp,
                    color = Color(0xFFDCDBDB)
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Choose Your Plan",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Select the perfect plan for your needs",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Basic Plan
            SubscriptionPlanCard(
                planName = "Basic",
                price = "$0",
                backgroundColor = MaterialTheme.colorScheme.background,
                buttonText = "Get Started",
                buttonColor = primary,
                textColor = Color.Black,
                features = listOf(
                    "Basic analytics",
                    "Limited storage (5GB)",
                    "Standard support",
                    "Single device access"
                ),
                isPopular = false,
                onClick = { navController.navigate(Screen.Home.route) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Plan
            SubscriptionPlanCard(
                planName = "Premium",
                price = "$1.99",
                backgroundColor = MaterialTheme.colorScheme.background,
                buttonText = if (subscriptionTier=="premium")
                "Already Subscribed" else "Upgrade Now",
                buttonColor = primary,
                textColor = if (subscriptionTier=="premium") Color(0xFFEACB02) else Color.White,
                features = listOf(
                    "Advanced analytics",
                    "Unlimited storage",
                    "Priority support 24/7",
                    "Multi-device access",
                    "Custom integrations",
                    "API access"
                ),
                isPopular = true,
                onClick = {
                    if (subscriptionTier=="premium") { }
                    else { navController.navigate(Screen.PaymentScreen.route) }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = "Guarantee",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "30-Day Money Back Guarantee",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            TextButton(
                onClick = { /* Handle FAQ click */ },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Help,
                    contentDescription = "FAQ",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SubscriptionPlanCard(
    planName: String,
    price: String,
    backgroundColor: Color,
    buttonText: String,
    buttonColor: Color,
    textColor: Color,
    features: List<String>,
    isPopular: Boolean,
    onClick: () -> Unit,
) {

    val primary = Color(0xFF2196F3)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            if (isPopular) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(16.dp))
                        .background(primary)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Most Popular",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = planName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "/month",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Features
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Feature included",
                        tint = Color(0xFF8D6E63),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Button
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = buttonText,
                    color = if (planName == "Basic") Color.Black else textColor,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionScreenPreview() {
    AutoComposeTheme {
        SubscriptionScreen(rememberNavController(), AutoComposeViewmodel())
    }
}