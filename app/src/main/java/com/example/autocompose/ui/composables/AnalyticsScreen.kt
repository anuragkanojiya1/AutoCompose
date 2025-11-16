package com.example.autocompose.ui.composables

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocompose.ui.viewmodel.AutoComposeViewmodel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.autocompose.data.datastore.PreferencesManager
import com.example.autocompose.ui.components.BottomBar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(autoComposeViewmodel: AutoComposeViewmodel, navController: NavController) {
    val scrollState = rememberScrollState()

    val popularModels = autoComposeViewmodel.topModels.collectAsState()
    val topLanguages = autoComposeViewmodel.topLanguages.collectAsState()
    val topTones = autoComposeViewmodel.topTones.collectAsState()

    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    val preferencesManager = PreferencesManager(context)

    val subscriptionTier by preferencesManager.subscriptionTierFlow.collectAsState(initial = "free")

    Log.d("AnalyticsScreen", "popularModels: ${popularModels}")
    Log.d("AnalyticsScreen", "topLanguages: $topLanguages")
    Log.d("AnalyticsScreen", "topTones: $topTones")

    // Effect to load trends data when the screen is displayed
    LaunchedEffect(Unit) {
        autoComposeViewmodel.getTrends()
    }

    // Set loading to false when data is available
    LaunchedEffect(
        popularModels.value,
        topLanguages.value,
        topTones.value
    ) {
        if (popularModels.value.isNotEmpty() &&
            topLanguages.value.isNotEmpty() &&
            topTones.value.isNotEmpty()
        ) {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if(subscriptionTier=="premium") {
                            Text("AutoCompose ✨")
                        } else{
                            Text("AutoCompose")
                        }
                    },
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
                HorizontalDivider(modifier = Modifier,
                    thickness = 0.65.dp,
                    color = Color(0xFFDCDBDB)
                )
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    BottomBar(navController)
                }
            )
        },
        modifier = Modifier.background(color = Color.White)
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.padding(top = 4.dp))

            // Dashboard Title and Date Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trends Dashboard",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .clip(RoundedCornerShape(4.dp))
//                    .background(Color(0xFFF5F5F5))
//                    .padding(8.dp)
//            ) {
//                Text(
//                    text = "Last 7 Days ▼",
//                    fontSize = 14.sp,
//                    color = Color.DarkGray
//                )
//            }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Popular Models Section
            Text(
                text = "Model Popularity Trends",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Show shimmer effect or actual content based on loading state
            if (isLoading) {
                // Shimmer loading for models
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerModelCard(modifier = Modifier.weight(1f))
                    ShimmerModelCard(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerModelCard(modifier = Modifier.weight(1f))
                    ShimmerModelCard(modifier = Modifier.weight(1f))
                }
            } else {
                // Popular Models Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Use the first two models from the list for the first row
                    val models = popularModels.value
                    val totalCount = models.sumOf { it.count }.toFloat()

                    if (models.size >= 1) {
                        val firstModel = models[0]
                        val secondModel = models[1]

                        Log.d("Model", models[0].toString())
                        Log.d("Model", models[1].toString())
                        Log.d("Model", models[2].toString())
                        // First model card
                        ModelCard(
                            name = firstModel.model,
                            percentage = "%.2f%%".format((firstModel.count.toFloat() / totalCount) * 100),
//                    color = Color(0xFFF0F4F8),
                            color = Color(0xFFF8F7F7),
                            icon = getModelIcon(firstModel.model),
                            modifier = Modifier.weight(1f)
                        )

                        // Second model card
                        ModelCard(
                            name = secondModel.model,
                            percentage = "%.2f%%".format((secondModel.count.toFloat() / totalCount) * 100),
//                    color = Color(0xFFFFF8F0),
                            color = Color(0xFFF8F7F7),
                            icon = getModelIcon(secondModel.model),
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Fallback to default model cards if not enough data
                        ModelCard(
                            name = "GPT-4",
                            percentage = "43%",
//                    color = Color(0xFFF0F4F8),
                            color = Color(0xFFF8F7F7),
                            icon = "⚙️",
                            modifier = Modifier.weight(1f)
                        )

                        ModelCard(
                            name = "DALL-E",
                            percentage = "27%",
//                    color = Color(0xFFFFF8F0),
                            color = Color(0xFFF8F7F7),
                            icon = "🖼️",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Use the next two models from the list for the second row
                    val models = popularModels.value
                    val totalCount = models.sumOf { it.count }.toFloat()

                    if (models.size >= 2) {
                        val thirdModel = models[2]
//                val fourthModel = models[0]

                        // Third model card
                        ModelCard(
                            name = thirdModel.model,
                            percentage = "%.2f%%".format((thirdModel.count.toFloat() / totalCount) * 100),
//                    color = Color(0xFFFFF0F5),
                            color = Color(0xFFF8F7F7),
                            icon = getModelIcon(thirdModel.model),
                            modifier = Modifier.weight(1f)
                        )

//                // Fourth model card
//                ModelCard(
//                    name = fourthModel.model,
//                    percentage = "${((fourthModel.count.toFloat() / totalCount) * 100).toInt()}%",
//                    color = Color(0xFFF0FFF5),
//                    icon = getModelIcon(fourthModel.model),
//                    modifier = Modifier.weight(1f)
//                )
                    } else {
                        // Fallback to default model cards if not enough data
                        ModelCard(
                            name = "Claude",
                            percentage = "18%",
//                    color = Color(0xFFFFF0F5),
                            color = Color(0xFFF8F7F7),
                            icon = "💬",
                            modifier = Modifier.weight(1f)
                        )

                        ModelCard(
                            name = "Stable Diffusion",
                            percentage = "12%",
//                    color = Color(0xFFF0FFF5),
                            color = Color(0xFFF8F7F7),
                            icon = "🎨",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Language Usage Section
            Text(
                text = "Global Language Trends",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                // Shimmer loading for pie chart
                ShimmerPieChart(modifier = Modifier.fillMaxWidth())
            } else {
                val languages = topLanguages.value.map { lang ->
                    val totalCount = topLanguages.value.sumOf { it.count }.toFloat()
                    val percentage =
                        if (totalCount > 0) (lang.count.toFloat() / totalCount) * 100f else 0f

                    Log.d(
                        "LanguageDebug",
                        "Processing language: ${lang.language}, count: ${lang.count}, percentage: $percentage"
                    )

                    PieChartEntry(
                        value = percentage,
                        label = lang.language,
                        color = getLanguageColor(lang.language)
                    )
                }.also {
                    // This ensures we have entries for visualization
                    Log.d(
                        "LanguageDebug",
                        "Final language entries passed to chart: ${it.map { entry -> "${entry.label}:${entry.value}%" }}"
                    )
                    if (it.isEmpty()) {
                        Log.w("LanguageDebug", "No language entries were created from the data!")
                    }
                }

                Log.d(
                    "LanguageDebug",
                    "topLanguages value before chart: ${topLanguages.value.size} items"
                )
                if (topLanguages.value.isEmpty()) {
                    Log.w("LanguageDebug", "topLanguages value is empty!")
                }

                // Check if French is present, if not add test data
                val hasFrench = languages.any { it.label.lowercase(Locale.ROOT) == "french" }
                Log.d("LanguageDebug", "Has French in language entries: $hasFrench")

                // Only for debugging: Ensure French is visible in the UI
                val finalLanguages = if (languages.isEmpty()) {
                    // If no languages at all, use demo data
                    Log.d("LanguageDebug", "No languages available, using demo data")
                    listOf(
                        PieChartEntry(
                            value = 75f,
                            label = "English",
                            color = getLanguageColor("English")
                        ),
                        PieChartEntry(
                            value = 25f,
                            label = "French",
                            color = getLanguageColor("French")
                        )
                    )
                } else if (!hasFrench) {
                    // If there's no French specifically, add it
                    Log.d("LanguageDebug", "Adding French entry for debugging")
                    languages + PieChartEntry(
                        value = 15f,
                        label = "French",
                        color = getLanguageColor("French")
                    )
                } else {
                    languages
                }

                Log.d(
                    "LanguageDebug",
                    "FINAL LANGUAGES being used in chart: ${finalLanguages.map { "${it.label}:${it.value}%" }}"
                )

                LanguagePieChart(
                    modifier = Modifier
                        .fillMaxWidth(),
                    languages = finalLanguages
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Communication Tones Section
            Text(
                text = "Tone Trends",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                // Shimmer loading for tone bars
                ShimmerTonesBarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            } else {
                // Display tone bars
                TonesBarChart(
                    tones = topTones.value.map { tone ->
                        // Calculate percentage from counts
                        val totalCount = topTones.value.sumOf { it.count }.toFloat()
                        val percentage =
                            if (totalCount > 0) (tone.count.toFloat() / totalCount) * 100f else 0f

                        ToneEntry(
                            label = tone.tone,
                            value = percentage,
                            color = getToneColor(tone.tone)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ModelCard(
    name: String,
    percentage: String,
    color: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Text(
                text = percentage,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
fun LanguagePieChart(
    modifier: Modifier = Modifier,
    languages: List<PieChartEntry> = listOf(
        PieChartEntry(value = 65f, label = "English", color = Color(0xFF8D6E63)),
        PieChartEntry(value = 20f, label = "Spanish", color = Color(0xFFAFB42B)),
        PieChartEntry(value = 15f, label = "French", color = Color(0xFF7E57C2))
    ),
) {
    // Use provided languages or defaults if empty
    val entries = if (languages.isEmpty()) {
        listOf(
            PieChartEntry(value = 65f, label = "English", color = Color(0xFF8D6E63)),
            PieChartEntry(value = 20f, label = "Spanish", color = Color(0xFFAFB42B)),
            PieChartEntry(value = 15f, label = "French", color = Color(0xFF7E57C2))
        )
    } else {
        languages.sortedByDescending { it.value } // Sort by value to ensure we show the most important ones
    }

    Log.d(
        "LanguageDebug",
        "Final language entries for chart: ${entries.map { "${it.label}:${it.value}%" }}"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dynamic angle-based pie chart
            Canvas(modifier = Modifier.size(180.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val center = Offset(canvasWidth / 2, canvasHeight / 2)
                val radius = minOf(canvasWidth, canvasHeight) / 2

                var startAngle = 0f
                val total = entries.sumOf { it.value.toDouble() }.toFloat()

                entries.forEach { entry ->
                    val sweepAngle = 360f * (entry.value / total)

                    drawArc(
                        color = entry.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = center - Offset(radius, radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    startAngle += sweepAngle
                }

                // Draw inner circle for donut effect
                drawCircle(
                    color = Color.White,
                    radius = radius * 0.6f,
                    center = center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend for the pie chart - show all entries
            Spacer(modifier = Modifier.height(16.dp))

            entries.forEach { entry ->
                Log.d(
                    "LanguageDebug",
                    "Rendering legend for: ${entry.label}, value: ${entry.value}"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(entry.color, CircleShape)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = entry.label,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "%.2f%%".format(entry.value),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun TonesBarChart(
    tones: List<ToneEntry>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        tones.forEach { tone ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tone.label,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "%.2f%%".format(tone.value),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bar representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(tone.value / 100f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(tone.color)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ShimmerModelCard(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(10f, 10f),
        end = Offset(translateAnimation.value, translateAnimation.value)
    )

    Card(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Shimmer for icon
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Shimmer for name
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Shimmer for percentage
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(16.dp)
                    .background(brush)
            )
        }
    }
}

@Composable
fun ShimmerPieChart(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(10f, 10f),
        end = Offset(translateAnimation.value, translateAnimation.value)
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shimmer for pie chart
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Shimmer for legend items
            repeat(3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(brush, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(14.dp)
                                .background(brush)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(14.dp)
                            .background(brush)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun ShimmerTonesBarChart(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(10f, 10f),
        end = Offset(translateAnimation.value, translateAnimation.value)
    )

    Column(
        modifier = modifier
    ) {
        repeat(4) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shimmer for tone label
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(14.dp)
                            .background(brush)
                    )

                    // Shimmer for percentage
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(14.dp)
                            .background(brush)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Shimmer for bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

data class ToneEntry(
    val label: String,
    val value: Float,
    val color: Color,
)

data class PieChartEntry(
    val value: Float,
    val label: String,
    val color: Color,
)

fun getModelIcon(modelName: String): String {
    return when (modelName) {
        "Gemini" -> "💎"
        "Mistral" -> "🌀"
        "Llama" -> "🦙"
        "GPT-4" -> "⚙️"
        "DALL-E" -> "🖼️"
        "Claude" -> "💬"
        "Stable Diffusion" -> "🎨"
        else -> "🤖"
    }
}

fun getLanguageColor(language: String): Color {
    return when (language.lowercase(Locale.ROOT)) {
        "english" -> Color(0xFF2196F3).copy(alpha = 1.0f)
        "spanish" -> Color(0xFF2196F3).copy(alpha = 0.9f)
        "french" -> Color(0xFF2196F3).copy(alpha = 0.8f)
        "japanese" -> Color(0xFF2196F3).copy(alpha = 0.7f)
        "german" -> Color(0xFF0D47A1)
        "italian" -> Color(0xFF1565C0)
        "chinese" -> Color(0xFF1976D2)
        "portuguese" -> Color(0xFF42A5F5)
        "russian" -> Color(0xFF64B5F6)
        "hindi" -> Color(0xFF90CAF9)
        else -> Color(0xFF2196F3).copy(alpha = 0.5f)
    }
}

fun getToneColor(tone: String): Color {
    return when (tone) {
        "Professional" -> Color(0xFF2196F3)
        "Friendly" -> Color(0xFF64B5F6)
        "Formal" -> Color(0xFF1976D2)
        "Casual" -> Color(0xFF90CAF9)
        "Assertive" -> Color(0xFF0D47A1)
        "Creative" -> Color(0xFF42A5F5)
        "Technical" -> Color(0xFF1565C0)
        else -> Color(0xFF2196F3).copy(alpha = 0.5f)
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    AnalyticsScreen(autoComposeViewmodel = AutoComposeViewmodel(), rememberNavController())
}