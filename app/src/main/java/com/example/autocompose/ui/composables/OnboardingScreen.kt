import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.autocompose.R
import com.example.autocompose.data.datastore.PreferencesManager
import com.example.autocompose.ui.navigation.Screen
import com.example.autocompose.ui.theme.AutoComposeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    var color = Color(0xFF2196F3)
    if (isSystemInDarkTheme()){
        color = Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Logo
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp),
//            contentAlignment = Alignment.Center
//        ) {
//                Image(
//                    painter = painterResource(id = R.drawable.logo2),
//                    contentDescription = "Logo",
////                modifier = Modifier.size(120.dp),
//                    contentScale = ContentScale.Fit
//                )
//        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingCard(
                    image = R.drawable.designer9,
                    title = "Get more done with\nAutoCompose",
                    stats = listOf(
                        Stat("2x faster\ncommunication", "100%"),
                        Stat("40% less email\nwriting time", "60%"),
                        Stat("3x more\ninterviews from cold emails", "300%"),
                        Stat("Multilingual support", "English,\nSpanish,\nFrench")
                    )
                )

                1 -> OnboardingCard(
                    image = R.drawable.designer15,
                    title = "AI-Powered Email\nGeneration",
                    stats = listOf(
                        Stat("Smart AI models", "Gemini, Mistral, Llama"),
                        Stat("Context-aware", "Understands intent"),
                        Stat("Multiple tones", "Professional, Friendly"),
                        Stat("Voice input", "Speak your ideas")
                    )
                )

                2 -> OnboardingCard(
                    image = R.drawable.designer14,
                    title = "Boost Your\nProductivity",
                    stats = listOf(
                        Stat("Save drafts", "Access anytime"),
                        Stat("Quick templates", "Ready formats"),
                        Stat("Analytics", "Track patterns"),
                        Stat("Gmail integration", "Send directly")
                    )
                )
            }
        }

        // Page indicators
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }

        // Get started button
        Button(
            onClick = {
                coroutineScope.launch {
                    preferencesManager.saveOnboardingStatus(true)
                    navController.navigate(Screen.LogIn.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = color
//                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Get started",
                color = MaterialTheme.colorScheme.background,
//                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun OnboardingCard(
    image: Int,
    title: String,
    stats: List<Stat>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "Image for onboarding",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                )
        }
            Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(stats[0].description, stats[0].value, Modifier.weight(1f))
                StatCard(stats[1].description, stats[1].value, Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(stats[2].description, stats[2].value, Modifier.weight(1f))
                StatCard(stats[3].description, stats[3].value, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(
    description: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(0.3.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(12.dp))
//            .background(Color(0xFFF2F2F7))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
    }
}

data class Stat(
    val description: String,
    val value: String,
)

@Preview(showBackground = true)
@Composable
fun OnboardScreenPreview() {
    AutoComposeTheme {
        OnboardingScreen(rememberNavController())
    }
}
