package com.buildsol.wordplaza.view.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ThumbDownAlt
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buildsol.wordplaza.ui.theme.AppTheme

@Composable
fun WordBox(
    word:String,
    meaning:String,
    synonyms:List<String>,
    antonyms:List<String>,
    egUse:String,
    saved:Boolean ,
    like:Int,
    dislike:Int,
    pronunciation:String? = null,
    onExpandedClick:(Boolean) -> Unit = {}
){
    var expanded = false
    val colors = MaterialTheme.colorScheme
    val wordColor by animateColorAsState(targetValue = if(expanded) colors.primary else colors.onSurfaceVariant)
    val rotation by animateFloatAsState(targetValue = if(expanded) 0f else 180f )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){

        // 🔹 Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleLarge,
                    color = wordColor
                )

                pronunciation?.let{
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { expanded = !expanded }
            ){
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colors.primaryContainer.copy(0.25f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = "Meaning",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.primary.copy(alpha = 0.8f)
                )

                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface
                )
            }
        }

        // Synonyms & Antonyms
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.fillMaxWidth()
        ){
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Antonyms",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.secondary
                    )
                    Text(
                        text = antonyms.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurface
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Synonyms",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.tertiary
                    )
                    Text(
                        text = synonyms.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurface
                    )
                }
            }
        }

        // Example Sentence
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.fillMaxWidth()
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceVariant)
                    .drawBehind {
                        val strokeWidth = 4.dp.toPx()
                        drawLine(
                            color = colors.primary,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
                    .padding(10.dp)
            ) {
                Text(
                    text = egUse,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = colors.onSurfaceVariant
                )
            }
        }

        // 🔹 Divider
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.fillMaxWidth()
        ){
            Canvas(modifier = Modifier.fillMaxWidth()) {
                drawLine(
                    color = colors.outlineVariant,
                    strokeWidth = 1.dp.toPx(),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f)
                )
            }
        }

        // 🔹 Bottom Actions
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.fillMaxWidth()
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

                    Icon(
                        imageVector = Icons.Default.ThumbUpAlt,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant
                    )
                    Text(
                        text = formatCount(like),
                        color = colors.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = Icons.Default.ThumbDownAlt,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant
                    )
                    Text(
                        text = formatCount(dislike),
                        color = colors.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (saved) Icons.Default.Bookmark else Icons.Outlined.Bookmark,
                    contentDescription = null,
                    tint = if (saved) colors.primary else colors.onSurfaceVariant // ✅ nice touch
                )
            }
        }
    }
}

private fun formatCount(value: Int): String {
    return when {
        value >= 1_000_000 -> String.format("%.1fm", value / 1_000_000f)
        value >= 1_000 -> String.format("%.1fk", value / 1_000f)
        else -> value.toString()
    }.replace(".0", "")
}

@Preview(showBackground = true)
@Composable
fun WordBoxPreview() {
    AppTheme(true) {
        WordBox(
            word = "Serendipity",
            meaning = "The occurrence of events by chance in a happy or beneficial way.",
            synonyms = listOf("fluke", "chance", "fortune"),
            antonyms = listOf("misfortune", "bad luck"),
            egUse = "Finding that book was pure serendipity.",
            saved = true,
            like = 120,
            dislike = 10,
            pronunciation = "/ˌserənˈdipədē/",
            onExpandedClick = {}
        )
    }
}