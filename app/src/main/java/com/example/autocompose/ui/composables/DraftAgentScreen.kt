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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftAgentScreen(
    autoComposeViewmodel: AutoComposeViewmodel,
    frequentEmailViewModel: FrequentEmailViewModel,
    passSubject: String,
    passEmailContent: String
) {
    Log.d("DraftAgentScreen", "Initialized with subject: $passSubject and email content: $passEmailContent")
    val primaryBlue = Color(0xFF2196F3)
    var recipientEmail by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("English") }
    var languageExpanded by remember { mutableStateOf(false) }
    var selectedTone by remember { mutableStateOf("Professional") }
    var selectedModel by remember { mutableStateOf("DeepSeek") }
    var subject by remember { mutableStateOf(passSubject) }
    var emailContent by remember { mutableStateOf(passEmailContent) }
    var emailContext by remember { mutableStateOf("") }

    Log.d("subject and body", "Subject $subject and email content $emailContent initialized")

    val generatedEmail = autoComposeViewmodel.generatedEmail.collectAsState()
    val emailSubject = autoComposeViewmodel.subject.collectAsState()

    val context = LocalContext.current

    val speechContext = context as MainActivity

    LaunchedEffect(speechContext.speechInput.value) {
        if (speechContext.speechInput.value.isNotBlank()) {
            emailContext = speechContext.speechInput.value
            speechContext.speechInput.value = ""
        }
    }

    fun createEmailIntent(): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            setPackage("com.google.android.gm")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, emailContent)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("AutoCompose") },
                    actions = {
                        IconButton(
                            onClick = {
                                // Delete the current email if it exists in database
                                frequentEmailViewModel.deleteEmailByContent(subject, emailContent)
                                // Navigate back
                                (context as? MainActivity)?.onBackPressedDispatcher?.onBackPressed()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Recycling,
                                contentDescription = "Delete Email",
                                tint = primaryBlue
                            )
                        }
                    },
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
                // Recipient email field
                OutlinedTextField(
                    value = recipientEmail,
                    onValueChange = { recipientEmail = it },
                    label = { Text("To: Recipient's email", color = Color.Gray) },
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
                            val models = listOf("DeepSeek", "Gemini", "Mistral")
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

                // Subject field
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

                // Email content
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
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
                            fontFamily = FontFamily.Serif,
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
                            containerColor = MaterialTheme.colorScheme.surfaceContainer                        ),
                        shape = RoundedCornerShape(16.dp),
                    )
                }


//                Button(
//                    onClick = {
//                        autoComposeViewmodel.generateEmail(
//                            tone = selectedTone,
//                            ai_model = selectedModel,
//                            language = language,
//                            context = emailContext
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = primaryBlue
//                    )
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Loop,
//                            contentDescription = "Generate Email"
//                        )
//                        Text("Generate")
//                    }
//                }

                // Bottom buttons
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            autoComposeViewmodel.generateEmail(
                                tone = selectedTone,
                                ai_model = selectedModel,
                                language = language,
                                context = emailContext
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2196F3)),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Loop,
                                contentDescription = "Generate Email",
                                tint = Color(0xFF2196F3)
                            )
                            Text("Generate", color = Color(0xFF2196F3))
                        }
                    }
//                    Button(
//                        onClick = { /* Save draft action */ },
//                        modifier = Modifier.weight(1f),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.White,
//                            contentColor = Color.Black,
//                        ),
//                        border = BorderStroke(1.dp, Color(0xFF2196F3)),
//                    ) {
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Sharp.Message,
//                                contentDescription = "Save Draft",
//                                tint = Color(0xFF2196F3)
//                            )
//                            Text("Save Draft", color = Color(0xFF2196F3))
//                        }
//                    }

                    Button(
                        onClick = {
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
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
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

@Preview(showBackground = true)
@Composable
fun DraftAgentScreenPreview() {
    AutoComposeTheme {
        AgentScreen(
            autoComposeViewmodel = AutoComposeViewmodel(),
            frequentEmailViewModel = FrequentEmailViewModel(Application())
        )
    }
}