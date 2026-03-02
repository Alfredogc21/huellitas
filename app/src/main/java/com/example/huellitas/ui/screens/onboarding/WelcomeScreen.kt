package com.example.huellitas.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huellitas.ui.components.PageIndicator
import com.example.huellitas.ui.theme.GradientDarkEnd
import com.example.huellitas.ui.theme.GradientDarkStart
import com.example.huellitas.ui.theme.GradientEnd
import com.example.huellitas.ui.theme.GradientStart
import com.example.huellitas.ui.theme.HuellitasTheme
import com.example.huellitas.ui.theme.PrimaryLight

/**
 * First onboarding screen — branded welcome with gradient background.
 *
 * Shows the app logo, name, and tagline over a warm gradient.
 * Only displayed during the first launch of the app.
 *
 * @param onNext Callback to navigate to the introduction screen
 */
@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val gradientColors = if (isDark) {
        listOf(GradientDarkStart, GradientDarkEnd)
    } else {
        listOf(GradientStart, GradientEnd)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(gradientColors))
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // ── Logo ──
            Surface(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Pets,
                    contentDescription = "Huellitas logo",
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.surface
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Title ──
            Text(
                text = "Huellitas",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Tagline ──
            Text(
                text = "Cada huella cuenta.\nAyuda a encontrar hogar a quienes más lo necesitan.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Page Indicator ──
            PageIndicator(
                totalPages = 3,
                currentPage = 0,
                activeColor = MaterialTheme.colorScheme.surface,
                inactiveColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── CTA Button ──
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryLight
                )
            ) {
                Text(
                    text = "Comenzar",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    HuellitasTheme {
        WelcomeScreen(onNext = {})
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WelcomeScreenDarkPreview() {
    HuellitasTheme(darkTheme = true) {
        WelcomeScreen(onNext = {})
    }
}
