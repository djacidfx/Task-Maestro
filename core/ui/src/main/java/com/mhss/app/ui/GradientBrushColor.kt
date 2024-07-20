package com.mhss.app.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mhss.app.ui.theme.MyBrainTheme
import com.mhss.app.ui.theme.Purple


fun gradientBrushColor(
    backgroundColor: Color,
    primaryColor: Color,
    secondaryColor: Color,
) = Brush.linearGradient(
    colorStops = arrayOf(
        0f to backgroundColor,
        0.5f to backgroundColor.copy(alpha = 0.4f).compositeOver(
            primaryColor
        ),
        1f to backgroundColor.copy(alpha = 0.4f).compositeOver(
            secondaryColor
        )
    ),
    start = Offset.Zero,
    end = Offset.Infinite
)

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun GradientColorPreview() {
    MyBrainTheme(useDynamicColors = false) {
        Box(
            Modifier
                .size(100.dp)
                .background(
                    gradientBrushColor(
                        MaterialTheme.colorScheme.background,
                        Color.Cyan,
                        Purple
                    )
                )
        )
    }
}