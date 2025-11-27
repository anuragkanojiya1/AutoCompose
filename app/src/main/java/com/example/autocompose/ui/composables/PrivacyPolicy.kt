package com.example.autocompose.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicy(){

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Privacy Policy", fontSize = 36.sp,
            fontWeight = FontWeight.W500, textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(20.dp))
        Text("Privacy Policy for AutoCompose\n" +
                "\n" +
                "Last Updated: 25 November 2025\n" +
                "\n" +
                "AutoCompose (“we”, “our”, or “the App”) is an AI-powered content generation application designed to help users create emails, messages, and other written content using advanced AI technology. This Privacy Policy explains how we collect, use, store, and protect your information when you use the App.\n" +
                "\n" +
                "By using AutoCompose, you agree to the terms outlined in this Privacy Policy.")
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyPreview(){
    PrivacyPolicy()
}