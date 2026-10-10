package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanNeon

@Composable
fun VisualizerBarPreview(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 16,
    barWidth: Dp = 3.dp,
    maxHeight: Dp = 28.dp,
    barColor: Color = CyanNeon
) {
    val transition = rememberInfiniteTransition(label = "visualizer_anim")

    val anim1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "v1"
    )

    val anim2 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "v2"
    )

    val anim3 by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "v3"
    )

    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(barCount) { index ->
            val factor = when (index % 4) {
                0 -> anim1
                1 -> anim3
                2 -> anim2
                else -> (anim1 + anim2) / 2f
            }
            val heightFraction = if (isPlaying) factor else 0.15f

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .fillMaxHeight(fraction = heightFraction.coerceIn(0.1f, 1f))
                    .clip(RoundedCornerShape(topStart = 1.5.dp, topEnd = 1.5.dp))
                    .background(barColor)
            )
        }
    }
}
