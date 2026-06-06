package com.buildsol.wordplaza.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildsol.wordplaza.model.Word

data class AddWordFormState(
    val word: String = "",
    val meaning: String = "",
    val synonyms: String = "",
    val antonyms: String = "",
    val example: String = ""
) {
    val canSubmit: Boolean
        get() = word.isNotBlank() && meaning.isNotBlank()

    fun toWord(): Word {
        return Word(
            word = word.trim(),
            meaning = meaning.trim(),
            synonyms = synonyms.toWordList(),
            antonyms = antonyms.toWordList(),
            egUse = example.trim()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordBottomSheet(
    isPosting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onPostWord: (Word) -> Unit
) {
    var formState by remember { mutableStateOf(AddWordFormState()) }

    ModalBottomSheet(
        onDismissRequest = {
            if (!isPosting) onDismiss()
        },
        containerColor = SheetBackground,
        contentColor = SheetText,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 18.dp, bottom = 8.dp)
                    .size(width = 58.dp, height = 7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4A4656))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Add New Word",
                    color = SheetText,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black
                )
                IconButton(
                    onClick = onDismiss,
                    enabled = !isPosting
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SheetText
                    )
                }
            }

            AddWordField(
                label = "WORD",
                value = formState.word,
                placeholder = "Enter word or phrase",
                required = true,
                enabled = !isPosting,
                trailingContent = {
                    AiFillPill()
                },
                onValueChange = { formState = formState.copy(word = it) }
            )

            AddWordField(
                label = "MEANING",
                value = formState.meaning,
                placeholder = "What does it mean?",
                required = true,
                enabled = !isPosting,
                minLines = 3,
                onValueChange = { formState = formState.copy(meaning = it) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AddWordField(
                    label = "SYNONYMS",
                    value = formState.synonyms,
                    placeholder = "Similar words",
                    enabled = !isPosting,
                    modifier = Modifier.weight(1f),
                    onValueChange = { formState = formState.copy(synonyms = it) }
                )
                AddWordField(
                    label = "ANTONYMS",
                    value = formState.antonyms,
                    placeholder = "Opposite words",
                    enabled = !isPosting,
                    modifier = Modifier.weight(1f),
                    onValueChange = { formState = formState.copy(antonyms = it) }
                )
            }

            AddWordField(
                label = "EXAMPLE SENTENCE",
                value = formState.example,
                placeholder = "Use it in a sentence...",
                enabled = !isPosting,
                minLines = 3,
                onValueChange = { formState = formState.copy(example = it) }
            )

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = ErrorText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { onPostWord(formState.toWord()) },
                enabled = formState.canSubmit && !isPosting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .background(
                        if (formState.canSubmit) PostButtonBrush else DisabledButtonBrush,
                        RoundedCornerShape(18.dp)
                    )
            ) {
                if (isPosting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Post Word",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            TextButton(
                onClick = onDismiss,
                enabled = !isPosting,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Cancel",
                    color = SheetText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun AddWordField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    enabled: Boolean = true,
    minLines: Int = 1,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (required) "$label *" else label,
                color = LabelText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            trailingContent?.invoke()
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            shape = RoundedCornerShape(18.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = PlaceholderText,
                    fontWeight = FontWeight.SemiBold
                )
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SheetText,
                unfocusedTextColor = SheetText,
                disabledTextColor = SheetText.copy(alpha = 0.55f),
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground,
                disabledContainerColor = FieldBackground,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                cursorColor = LabelText
            )
        )
    }
}

@Composable
private fun AiFillPill() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF0D2B3F).copy(alpha = 0.8f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AutoFixHigh,
            contentDescription = null,
            tint = Color(0xFF9ED0FF),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "AI FILL",
            color = Color(0xFFC5DDFF),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
    }
}

private fun String.toWordList(): List<String> {
    return split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
}

private val SheetBackground = Color(0xFF111111)
private val SheetText = Color(0xFFF2EEFF)
private val FieldBackground = Color(0xFF202020)
private val LabelText = Color(0xFFC8BFFF)
private val PlaceholderText = Color(0xFF586474)
private val ErrorText = Color(0xFFFF8A99)
private val PostButtonBrush = Brush.linearGradient(listOf(Color(0xFF7057F1), Color(0xFF6551E8)))
private val DisabledButtonBrush = Brush.linearGradient(listOf(Color(0xFF3B3946), Color(0xFF34323D)))
