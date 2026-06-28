package com.buildsol.wordplaza.view.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.ThumbDownAlt
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buildsol.wordplaza.model.PostUserState
import com.buildsol.wordplaza.model.WordPost
import com.buildsol.wordplaza.ui.theme.AppTheme

@Composable
fun WordBox(
    word: WordPost,
    initialExpansion: Boolean = false,
    userState: PostUserState = PostUserState(postId = word.id),
    onLikeClick: () -> Unit = {},
    onDislikeClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {}
) {
    var expanded by remember(word.id) { mutableStateOf(initialExpansion) }
    val colors = MaterialTheme.colorScheme
    val wordColor by animateColorAsState(targetValue = if (expanded) colors.primary else colors.onSurfaceVariant)
    val rotation by animateFloatAsState(targetValue = if (expanded) 0f else 180f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (word.authorDisplayName.isNotBlank() || word.authorUsername.isNotBlank()) {
                    Text(
                        text = word.authorDisplayName.ifBlank { word.authorUsername },
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleLarge,
                    color = wordColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                word.pronunciation?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = colors.primary,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }

        if (word.caption.isNotBlank()) {
            Text(
                text = word.caption,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface
            )
        }

        AnimatedVisibility(visible = expanded, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primaryContainer.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Meaning",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = word.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface
                )
            }
        }

        AnimatedVisibility(visible = expanded, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WordListColumn(title = "Antonyms", values = word.antonyms, modifier = Modifier.weight(1f))
                WordListColumn(title = "Synonyms", values = word.synonyms, modifier = Modifier.weight(1f))
            }
        }

        AnimatedVisibility(visible = expanded && word.example.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
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
                    text = word.example,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = colors.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(visible = expanded, modifier = Modifier.fillMaxWidth()) {
            Canvas(modifier = Modifier.fillMaxWidth()) {
                drawLine(
                    color = colors.outlineVariant,
                    strokeWidth = 1.dp.toPx(),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f)
                )
            }
        }

        AnimatedVisibility(visible = expanded, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = if (userState.liked) Icons.Default.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                        contentDescription = "Like",
                        tint = if (userState.liked) colors.primary else colors.onSurfaceVariant,
                        modifier = Modifier.clickable { onLikeClick() }
                    )
                    Text(text = formatCount(word.likeCount), color = colors.onSurfaceVariant)

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = if (userState.disliked) Icons.Default.ThumbDownAlt else Icons.Outlined.ThumbDownAlt,
                        contentDescription = "Dislike",
                        tint = if (userState.disliked) colors.error else colors.onSurfaceVariant,
                        modifier = Modifier.clickable { onDislikeClick() }
                    )
                    Text(text = formatCount(word.dislikeCount), color = colors.onSurfaceVariant)
                }

                Icon(
                    imageVector = if (userState.saved) Icons.Default.Bookmark else Icons.Outlined.Bookmark,
                    contentDescription = "Save",
                    tint = if (userState.saved) colors.primary else colors.onSurfaceVariant,
                    modifier = Modifier.clickable { onBookmarkClick() }
                )
            }
        }
    }
}

@Composable
private fun WordListColumn(title: String, values: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = values.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "None added",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
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
            word = WordPost(
                id = "1",
                word = "Serendipity",
                meaning = "The occurrence of events by chance in a happy or beneficial way.",
                synonyms = listOf("fluke", "chance", "fortune"),
                antonyms = listOf("misfortune", "bad luck"),
                example = "Finding that book was pure serendipity.",
                likeCount = 120,
                dislikeCount = 10,
                pronunciation = "/ser-en-dip-i-tee/"
            ),
            userState = PostUserState(postId = "1", saved = true)
        )
    }
}
