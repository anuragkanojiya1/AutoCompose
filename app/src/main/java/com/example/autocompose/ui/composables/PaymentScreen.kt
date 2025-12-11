package com.example.autocompose.ui.composables

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.autocompose.ui.viewmodel.PaymentViewModel
import com.example.autocompose.utils.Constants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.autocompose.data.datastore.PreferencesManager
import com.example.autocompose.domain.model.UpdateSubscriptionRequest
import com.example.autocompose.ui.theme.AutoComposeTheme
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel,
    amount: String = "1.99"
) {
    val TAG = "PaymentScreen"
    Log.d(TAG, "Initializing PaymentScreen with amount: $$amount")

    val primaryBlue = Color(0xFF2196F3)

    val context = LocalContext.current
    val activity = context as Activity
    
    val paymentState by viewModel.paymentState.collectAsState()
    var termsAccepted by remember { mutableStateOf(false) }

    val preferencesManager = PreferencesManager(context)

    var token by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    token = task.result?.token ?: ""
                    Log.d("Token", token)
                } else {
                    Toast.makeText(context, "Token is null Login again please", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Fetch access token on initialization if not already available
    LaunchedEffect(Unit) {
        if (paymentState.accessToken == null) {
            Log.d(TAG, "No access token found, fetching from PayPal")
            viewModel.fetchAccessToken()
        } else {
            Log.d(TAG, "Using existing access token")
        }
    }

    val autoComposeViewmodel = AutoComposeViewmodel()

//    LaunchedEffect(paymentState.isSuccess) {
//        if (paymentState.isSuccess){
//            autoComposeViewmodel.updateSubscription( UpdateSubscriptionRequest("Premium"), token)
//        }
//    }

    // Check for intent from PayPal browser redirect
    LaunchedEffect(Unit) {
        activity.intent?.let { intent ->
            val data = intent.data
            if (data?.scheme == "com.example.autocompose" && data.host == "paypalpay") {
                // Extract opType from the URL if available
                val opType = data.getQueryParameter("opType")
                Log.d(TAG, "Received PayPal redirect with URI: ${data}, opType: $opType")
                viewModel.handlePayPalResult(opType, data)
            }
        }
    }

    // Log payment state changes
    LaunchedEffect(paymentState) {
        Log.d(
            TAG, "Payment state updated: isLoading=${paymentState.isLoading}, " +
                    "orderId=${paymentState.orderId}, isSuccess=${paymentState.isSuccess}, " +
                    "hasError=${paymentState.error != null}"
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search action */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(paddingValues),
        ) {
            // Price display section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryBlue)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$1.99/month",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.background
                    )
                    Text(
                        text = "AutoCompose Premium Subscription",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }

            // Payment method section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // PayPal option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Select PayPal */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // PayPal icon placeholder
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    primaryBlue,
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "P",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Pay with PayPal",
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Selected indicator
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(2.dp, primaryBlue, CircleShape)
                                .padding(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(primaryBlue)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Secure payment processed by PayPal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "You will be redirected to PayPal to complete your payment",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Order Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Order Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "AutoCompose Premium Monthly",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "$1.99",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Recurring payment",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Monthly",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Next billing date",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Jun 21, 2025",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Terms and conditions
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = primaryBlue,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    )
                    Text(
                        text = "I agree to the Terms of Service and Privacy Policy",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_lock_idle_lock),
                        contentDescription = "Lock",
                        tint = primaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "256-bit SSL Encrypted",
                        style = MaterialTheme.typography.bodySmall,
                        color = primaryBlue,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pay button
                Button(
                    onClick = {
                        // Keep existing payment logic
                        if (termsAccepted) {
                            Log.d(TAG, "User clicked 'Pay with PayPal' button")
                            viewModel.createOrder("1.99") { orderId ->
                                Log.d(TAG, "Order created with ID: $orderId")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    enabled = termsAccepted
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_input_get),
                        contentDescription = "PayPal",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Pay with PayPal",
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Preserve state-based behavior with hidden elements
            when {
                paymentState.isLoading -> {
                    // Original loading UI is hidden but logic remains
                }
                paymentState.error != null -> {
                    // Show error message in a more subtle way
                    Text(
                        text = "Error: ${paymentState.error}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Button(
                        onClick = {
                            Log.d(TAG, "User clicked 'Try Again' button")
                            viewModel.resetState()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(text = "Try Again")
                    }
                }

                paymentState.isSuccess -> {
                    // Success state handling preserved

//                    CoroutineScope(Dispatchers.IO).launch {
//                        preferencesManager.saveSubscriptionTier("premium")
//                        autoComposeViewmodel.updateSubscription(UpdateSubscriptionRequest("premium"), token)
//                    }

                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            preferencesManager.saveSubscriptionTier("premium")
                            autoComposeViewmodel.updateSubscription(
                                UpdateSubscriptionRequest("premium"),
                                token
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(
                                1.dp,
                                primaryBlue,
                                RoundedCornerShape(24.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Payment Successful!",
                                style = MaterialTheme.typography.titleMedium,
                                color = primaryBlue
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    Log.d(TAG, "User clicked 'Back to App' button")
                                    navController.popBackStack()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue
                                )
                            ) {
                                Text(text = "Back to App")
                            }
                        }
                    }
                }
                paymentState.orderId != null -> {
                    // Keep the redirect logic
                    LaunchedEffect(paymentState.orderId) {
                        val paypalUrl =
                            "https://paypal.com/checkoutnow?token=${paymentState.orderId}"
                        Log.d(TAG, "Launching PayPal URL: $paypalUrl")
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(paypalUrl)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PaymentScreenPreview() {
    AutoComposeTheme {
        PaymentScreen(rememberNavController(), viewModel = PaymentViewModel(), amount = "1.99")
    }
}