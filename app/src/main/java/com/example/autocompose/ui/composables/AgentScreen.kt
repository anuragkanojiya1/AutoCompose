package com.example.autocompose.ui.composables

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.Message
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocompose.MainActivity
import com.example.autocompose.ui.theme.AutoComposeTheme
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel
import com.example.autocompose.ui.viewmodel.FrequentEmailViewModel
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.autocompose.data.datastore.PreferencesManager
import com.example.autocompose.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentScreen(
    autoComposeViewmodel: AutoComposeViewmodel,
    frequentEmailViewModel: FrequentEmailViewModel,
    navController: NavController
) {
    val primaryBlue = Color(0xFF2196F3)

    var recipientEmails by remember { mutableStateOf(mutableListOf("")) }

    var languageExpanded by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf("Llama") }
    var subject by remember { mutableStateOf("") }
    var emailContent by remember { mutableStateOf("") }
    var emailContext by remember { mutableStateOf("") }

    var count by remember { mutableStateOf(1) }

    val generatedEmail = autoComposeViewmodel.generatedEmail.collectAsState()
    val emailSubject = autoComposeViewmodel.subject.collectAsState()

    val context = LocalContext.current

    val speechContext = context as MainActivity

    var token by rememberSaveable { mutableStateOf("") }

    val preferencesManager = PreferencesManager(context)

    val defaultLanguage = preferencesManager.languageFlow.collectAsState(initial = "English")
    val defaultFontFamily = preferencesManager.fontFamilyFlow.collectAsState(initial = "Default")
    val defaultWritingStyle = preferencesManager.writingStyleFlow.collectAsState(initial = "Professional")

    Log.d("DefaultFont", defaultFontFamily.value.toString())

    var selectedTone by remember { mutableStateOf(defaultWritingStyle.value.toString()) }
    LaunchedEffect(defaultWritingStyle.value) {
        selectedTone = defaultWritingStyle.value.toString()
    }

    Log.d("defaultW", defaultWritingStyle.value)
    Log.d("WritingTone", selectedTone)

    var language by remember { mutableStateOf(defaultLanguage.value) }
    LaunchedEffect(defaultLanguage.value) {
        language = defaultLanguage.value
    }

    Log.d("defaultL", defaultLanguage.value)
    Log.d("Language", language.toString())

    val subscriptionState = autoComposeViewmodel.checkSubscription.collectAsState()

    val subscriptionTier by preferencesManager.subscriptionTierFlow.collectAsState(initial = "free")

    Log.d("SubscriptionTier", subscriptionTier)

    val subscription = subscriptionState.value

    LaunchedEffect(speechContext.speechInput.value) {
        if (speechContext.speechInput.value.isNotBlank()) {
            emailContext = speechContext.speechInput.value
            speechContext.speechInput.value = ""
        }
    }

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


        if (token.isNotBlank()) {
            LaunchedEffect(Unit) {
                autoComposeViewmodel.checkSubscription(token)
                Log.d("Subscription", subscription.toString())
            }
        }


    fun createEmailIntent(): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            setPackage("com.google.android.gm")
            putExtra(Intent.EXTRA_EMAIL, recipientEmails.toTypedArray())
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, emailContent)
        }
    }

    LaunchedEffect(emailSubject.value) {
        subject = emailSubject.value
    }

    LaunchedEffect(generatedEmail.value) {
        emailContent = generatedEmail.value
    }

    Log.d("Subscription", subscription.toString())

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
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                HorizontalDivider(modifier = Modifier,
                    thickness = 0.65.dp,
                    color = Color(0xFFDCDBDB)
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.padding(top = 2.dp))

                recipientEmails.forEachIndexed { index, email ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { newValue ->
                                val updatedList = recipientEmails.toMutableList()
                                updatedList[index] = newValue
                                recipientEmails = updatedList
                            },
                            label = { Text("Recipient Email ${index + 1}", color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedTextColor = Color.Gray,
                                unfocusedBorderColor = Color(0xFFE7E6E6),
                                focusedBorderColor = Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                Row {
                                    IconButton(
                                        onClick = {

                                            recipientEmails = recipientEmails.toMutableList().apply { add("") }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add recipient"
                                        )
                                    }

                                    if (recipientEmails.size > 1) {
                                        IconButton(
                                            onClick = {

                                                recipientEmails = recipientEmails.toMutableList().apply { removeAt(index) }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = "Remove recipient"
                                            )
                                        }
                                    }
                                }
                            }
                        )

//                        Row {
//                            IconButton(
//                                onClick = {
//
//                                    recipientEmails = recipientEmails.toMutableList().apply { add("") }
//                                }
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Add,
//                                    contentDescription = "Add recipient"
//                                )
//                            }
//
//                            if (recipientEmails.size > 1) {
//                                IconButton(
//                                    onClick = {
//
//                                        recipientEmails = recipientEmails.toMutableList().apply { removeAt(index) }
//                                    }
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Remove,
//                                        contentDescription = "Remove recipient"
//                                    )
//                                }
//                            }
//                        }
                    }
                }

//                    for (i in 1..count) {
//                        OutlinedTextField(
//                            value = recipientEmail,
//                            onValueChange = { recipientEmail = it },
//                            label = { Text("To: Recipient's email", color = Color.Gray) },
//                            modifier = Modifier.fillMaxWidth(),
//                            singleLine = true,
//                            colors = TextFieldDefaults.outlinedTextFieldColors(
//                                unfocusedTextColor = Color.Gray,
//                                unfocusedBorderColor = Color(0xFFE7E6E6),
//                                focusedBorderColor = primaryBlue
//                            ),
//                            shape = RoundedCornerShape(12.dp),
//                            trailingIcon = {
//                                IconButton(onClick = {
//                                    count++;
//                                }) {
//                                    Icon(
//                                        imageVector = Icons.Default.Add,
//                                        contentDescription = ""
//                                    )
//                                }
//                            }
//                        )
//                    }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            color = Color.LightGray,
                            width = 0.6.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Language row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = primaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "Language",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            ExposedDropdownMenuBox(
                                expanded = languageExpanded,
                                onExpandedChange = { languageExpanded = !languageExpanded },
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .menuAnchor()
                                        .width(88.dp)
                                ) {
                                    Text(
                                        text = language,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded)
                                }

                                ExposedDropdownMenu(
                                    expanded = languageExpanded,
                                    onDismissRequest = { languageExpanded = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("German") },
                                        onClick = {
                                            language = "German"
                                            languageExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("English") },
                                        onClick = {
                                            language = "English"
                                            languageExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Spanish") },
                                        onClick = {
                                            language = "Spanish"
                                            languageExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("French") },
                                        onClick = {
                                            language = "French"
                                            languageExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Japanese") },
                                        onClick = {
                                            language = "Japanese"
                                            languageExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // AI Model
                        Text(
                            "AI Model",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            val models = listOf("Gemini", "Mistral", "Llama")
                            models.forEach { model ->
                                FilterChip(
                                    selected = selectedModel == model,
                                    onClick = { selectedModel = model },
                                    label = { Text(model, textAlign = TextAlign.Center) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = primaryBlue,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFF8F7F7),
                                        labelColor = Color.Black,
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(
                                        width = 2.5.dp,
                                        color = if (model == "Mistral") Color(0xFFCBAF1A) else Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Tone
                        Text(
                            "Tone",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            val tones = listOf("Professional", "Friendly", "Formal")
                            tones.forEach { tone ->
                                FilterChip(
                                    selected = selectedTone == tone,
                                    onClick = { selectedTone = tone },
                                    label = { Text(tone, textAlign = TextAlign.Center) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = primaryBlue,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFF8F7F7),
                                        labelColor = Color.Black,
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = emailContext,
                    onValueChange = { emailContext = it },
                    label = { Text("Email Context", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedTextColor = Color.Gray,
                        unfocusedBorderColor = Color(0xFFE7E6E6),
                        focusedBorderColor = primaryBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                // Voice button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            speechContext.askSpeechInput(context)
                        },
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue
                        ),
                        contentPadding = ButtonDefaults.ContentPadding
                    ) {
                    }
                    Icon(
                        imageVector = Icons.Filled.MicNone,
                        contentDescription = "Voice Input",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedTextColor = Color.Gray,
                        unfocusedBorderColor = Color(0xFFE7E6E6),
                        focusedBorderColor = primaryBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                        //Color(0xFFF8F7F7)
                    )
                ) {
                    OutlinedTextField(
                        label = { Text("AI generated email content will appear here...",
                            color = Color.Gray) },
                        value = emailContent,
                        onValueChange = {
                            emailContent = it
                        },
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = when(defaultFontFamily.value.toString()) {
                                "FontFamily.Serif" -> FontFamily.Serif
                                "FontFamily.SansSerif" -> FontFamily.SansSerif
                                "FontFamily.Monospace" -> FontFamily.Monospace
                                "FontFamily.Cursive" -> FontFamily.Cursive
                                else -> FontFamily.Default
                            },
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .heightIn(min = 180.dp),
                        singleLine = false,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedTextColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = Color.DarkGray,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                    )
                }


                Button(
                    onClick = {
                        if (emailContext.isNotEmpty() && recipientEmails.isNotEmpty()) {
                            // Prevent sending "string" as a value
                            val validTone =
                                if (selectedTone == "string" || selectedTone.isBlank()) "Professional" else selectedTone
                            val validModel =
                                if (selectedModel == "string" || selectedModel.isBlank()) "Llama" else selectedModel
                            val validLanguage =
                                if (language == "string" || language.isBlank()) "English" else language

                            if (token.isNotBlank()) {

                                if (selectedModel!="Mistral"){
                                autoComposeViewmodel.generateEmail(
                                    tone = validTone,
                                    ai_model = validModel,
                                    language = validLanguage,
                                    context = emailContext,
                                    token = token
                                )}
                                else{
                                    if (subscription!="free" || subscriptionTier=="premium"){
                                        autoComposeViewmodel.generateEmail(
                                            tone = validTone,
                                            ai_model = validModel,
                                            language = validLanguage,
                                            context = emailContext,
                                            token = token
                                        )
                                    } else {
                                        navController.navigate(Screen.PaymentScreen.route)
                                        Toast.makeText(context, "You need a subscription to use Mistral", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Token is null Login again please", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Loop,
                            tint = Color.White,
                            contentDescription = "Generate Email"
                        )
                        Text("Generate", color = Color.White)
                    }
                }

                // Bottom buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (subject.isNotEmpty() && emailContent.isNotEmpty())
                            try {
                                // Save email to the database and increment frequency
                                frequentEmailViewModel.saveOrUpdateEmail(
                                    subject = emailSubject.value,
                                    emailBody = generatedEmail.value
                                )
                                Log.d("AgentScreen", "Saving email: '${emailSubject.value}'")
                                Log.d("AgentScreen", "Updated frequency in database")
//                                context.startActivity(createEmailIntent())
                            } catch (e: Exception) {
                                Log.e("AgentScreen", "Error saving email", e)
                                Toast.makeText(context, "Error saving email: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                            else
                                Toast.makeText(context, "Please generate subject and email content", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2196F3)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Sharp.Message,
                                contentDescription = "Save Draft",
                                tint = Color(0xFF2196F3)
                            )
                            Text("Save Draft", color = Color(0xFF2196F3))
                        }
                    }

                    Button(
                        onClick = {
                            if (subject.isNotEmpty() && emailContent.isNotEmpty())
                            try {
                                // Save email to the database and increment frequency
                                frequentEmailViewModel.saveOrUpdateEmail(
                                    subject = emailSubject.value,
                                    emailBody = generatedEmail.value
                                )
                                Log.d("AgentScreen", "Sending email with subject: '${emailSubject.value}'")
                                Log.d("AgentScreen", "Updated frequency in database")
                                context.startActivity(createEmailIntent())
                            } catch (e: ActivityNotFoundException) {
                                Log.e("AgentScreen", "Gmail app not installed!", e)
                                Toast.makeText(context, "Gmail app not installed!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Log.e("AgentScreen", "Error sending email", e)
                                Toast.makeText(context, "Error sending email: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                            else
                                Toast.makeText(context, "Please generate subject and email content", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                tint = Color.White,
                                contentDescription = "Send"
                            )
                            Text("Send", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModelRadioButton(text: String, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RadioButton(
            selected = text == selectedOption,
            onClick = { onOptionSelected(text) },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF155ADA)
            )
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AgentScreenPreview() {
    AutoComposeTheme {
        AgentScreen(
            autoComposeViewmodel = AutoComposeViewmodel(),
            frequentEmailViewModel = FrequentEmailViewModel(Application()),
            navController = rememberNavController()
        )
    }
}