package com.buildsol.wordplaza.view.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    expanded:Boolean,
    onExpandedClick:(Boolean) -> Unit = {}
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        // the word itself
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                pronunciation?.let{
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            IconButton(
                onClick = { onExpandedClick(expanded) }
            ){
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "The Expanding icon",
                    modifier = Modifier
                        .graphicsLayer(
                            rotationY = 180f
                        )
                )
            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(0.2f),
                    RoundedCornerShape(16.dp)
                )
                .clip(shape = RoundedCornerShape(16.dp))
                .padding(vertical = 8.dp, horizontal = 10.dp)
        ){
            Text(
                text = "Meaning",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary.copy(0.7f)
            )
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = "Antonyms",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = antonyms.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = "Synonyms",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = synonyms.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(0.5f)
                )
                .drawBehind {
                    val strokeWidth = 5.dp.toPx()

                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ){
            Text(
                text = egUse,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )
        }

        Canvas(
            modifier = Modifier
        ){
            val strokeWidth = 2.dp.toPx()
            drawLine(
                color = Color.White,
                strokeWidth = strokeWidth,
                start = Offset(0f,0f),
                end = Offset(0f,size.width)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()


        ){

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
            expanded = true,
            onExpandedClick = {}
        )
    }
}