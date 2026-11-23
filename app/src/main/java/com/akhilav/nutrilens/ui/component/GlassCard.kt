package com.akhilav.nutrilens.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.akhilav.nutrilens.ui.theme.DarkBackground
import com.akhilav.nutrilens.ui.theme.GlassBorderDark
import com.akhilav.nutrilens.ui.theme.GlassBorderLight
import com.akhilav.nutrilens.ui.theme.GlassDark
import com.akhilav.nutrilens.ui.theme.GlassLight

@Composable
fun GlassCard(
    modifier : Modifier = Modifier,
    content:@Composable ColumnScope.()-> Unit
){
    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    val shape = RoundedCornerShape(24.dp)
    val glassBrush = Brush.verticalGradient(
        if(isDark){
        listOf(GlassDark, MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
    } else {
        listOf(
            GlassLight,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
            )
        }
    )
    val borderColor = if(isDark) GlassBorderDark else GlassBorderLight

    Box(
        modifier = modifier
            .clip(shape)
            .background(glassBrush)
            .border(BorderStroke(1.dp, borderColor), shape)
    ) {
        Column(content = content,modifier = Modifier.fillMaxWidth())
    }

}