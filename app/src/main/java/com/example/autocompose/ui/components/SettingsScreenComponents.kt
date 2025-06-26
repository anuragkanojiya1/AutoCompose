package com.example.autocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun FontSelectionDialog(
    currentFont: String,
    onFontSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val fontOptions = listOf("Default", "Serif", "SansSerif", "Monospace")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Font") },
        text = {
            Column {
                fontOptions.forEach { font ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFontSelected(font) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (font) {
                                "Default" -> "Default"
                                "Serif" -> "Serif"
                                "SansSerif" -> "Sans Serif"
                                "Monospace" -> "Monospace"
                                else -> font
                            },
                            modifier = Modifier.weight(1f),
                            fontFamily = when (font) {
                                "Serif" -> FontFamily.Serif
                                "SansSerif" -> FontFamily.SansSerif
                                "Monospace" -> FontFamily.Monospace
                                else -> FontFamily.Default
                            }
                        )

                        if (font == currentFont) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val languageOptions = listOf("English", "Spanish", "French", "German", "Japanese")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language") },
        text = {
            Column {
                languageOptions.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language,
                            modifier = Modifier.weight(1f),
                        )

                        if (language == currentLanguage) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun WritingStyleSelectionDialog(
    currentWritingStyle: String,
    onWritingStyleSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val writingStyleOptions = listOf(
        "Professional", "Casual",
        "Formal", "Informal", "Impromptu"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Writing Style") },
        text = {
            Column {
                writingStyleOptions.forEach { writingStyle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onWritingStyleSelected(writingStyle)
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = writingStyle,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )

                        if (writingStyle == currentWritingStyle) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "$writingStyle selected",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
