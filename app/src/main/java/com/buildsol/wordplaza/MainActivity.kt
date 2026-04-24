package com.buildsol.wordplaza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildsol.wordplaza.ui.theme.AppTheme
import kotlinx.coroutines.launch

private enum class PlazaScreen(val label: String, val icon: String) {
    Feed("Feed", "F"),
    Add("Add", "+"),
    Profile("Profile", "P")
}

private data class WordPost(
    val id: Int,
    val word: String,
    val meaning: String,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val example: String,
    val username: String,
    val initials: String,
    val avatarColors: List<Color>,
    val likes: Int,
    val dislikes: Int,
    val comments: Int,
    val saves: Int,
    val helpful: Int,
    val incorrect: Int
)

private val Purple = Color(0xFF8B5CF6)
private val Blue = Color(0xFF38BDF8)
private val Orange = Color(0xFFFF9F43)
private val Ink = Color(0xFF211B35)
private val Muted = Color(0xFF736D85)
private val Canvas = Color(0xFFFFF8F3)
private val Card = Color(0xFFFFFCFA)
private val Lemon = Color(0xFFFFF1B8)
private val Mint = Color(0xFFDDFBE8)
private val Lilac = Color(0xFFEDE7FF)
private val Sky = Color(0xFFE5F6FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                WordPlazaApp()
            }
        }
    }
}

@Composable
private fun WordPlazaApp() {
    var selectedScreen by rememberSaveable { mutableStateOf(PlazaScreen.Feed) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Canvas,
        floatingActionButton = {
            if (selectedScreen != PlazaScreen.Add) {
                FloatingActionButton(
                    onClick = { selectedScreen = PlazaScreen.Add },
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .shadow(16.dp, RoundedCornerShape(18.dp), ambientColor = Purple.copy(alpha = 0.28f))
                        .background(AccentBrush, RoundedCornerShape(18.dp))
                ) {
                    Text("+", fontSize = 28.sp, fontWeight = FontWeight.Black)
                }
            }
        },
        bottomBar = {
            PlazaBottomBar(selectedScreen = selectedScreen, onSelected = { selectedScreen = it })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundBrush)
        ) {
            when (selectedScreen) {
                PlazaScreen.Feed -> FeedScreen()
                PlazaScreen.Add -> AddPostScreen()
                PlazaScreen.Profile -> ProfileScreen()
            }
        }
    }
}

@Composable
private fun PlazaBottomBar(selectedScreen: PlazaScreen, onSelected: (PlazaScreen) -> Unit) {
    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        PlazaScreen.entries.forEach { screen ->
            NavigationBarItem(
                selected = selectedScreen == screen,
                onClick = { onSelected(screen) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (selectedScreen == screen) AccentBrush else Brush.linearGradient(listOf(Sky, Lilac))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(screen.icon, color = if (selectedScreen == screen) Color.White else Purple, fontWeight = FontWeight.Black)
                    }
                },
                label = { Text(screen.label, fontWeight = FontWeight.SemiBold) }
            )
        }
    }
}

@Composable
private fun FeedScreen() {
    val posts = remember { samplePosts }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            FeedHeader()
        }
        items(posts, key = { it.id }) { post ->
            WordPostCard(post = post, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FeedHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("WordPlaza", color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("Swipe through ideas worth remembering", color = Muted, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AccentBrush),
            contentAlignment = Alignment.Center
        ) {
            Text("W", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun WordPostCard(post: WordPost, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable(post.id) { mutableStateOf(false) }
    var liked by rememberSaveable(post.id) { mutableStateOf(false) }
    var disliked by rememberSaveable(post.id) { mutableStateOf(false) }
    var saved by rememberSaveable(post.id) { mutableStateOf(false) }
    var pulse by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(pulse) {
        if (pulse) {
            scale.snapTo(0.78f)
            scale.animateTo(1.08f, tween(160, easing = FastOutSlowInEasing))
            scale.animateTo(1f, tween(160, easing = FastOutSlowInEasing))
            pulse = false
        }
    }

    Surface(
        color = Card,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 5.dp,
        modifier = modifier
            .pointerInput(post.id) {
                detectTapGestures(
                    onDoubleTap = {
                        liked = true
                        disliked = false
                        pulse = true
                    }
                )
            }
    ) {
        Box {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PostAuthor(post)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(post.word, color = Ink, fontSize = 34.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                    EngagementPill(text = "${post.helpful} helpful", background = Mint, color = Color(0xFF23845A))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(listOf(Lemon, Sky)))
                        .padding(16.dp)
                ) {
                    Text(post.meaning, color = Ink, fontSize = 18.sp, lineHeight = 25.sp, fontWeight = FontWeight.SemiBold)
                }
                AnimatedVisibility(visible = expanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ChipSection(title = "Synonyms", chips = post.synonyms, color = Lilac)
                        ChipSection(title = "Antonyms", chips = post.antonyms, color = Color(0xFFFFE3D6))
                        DetailBlock(label = "Example", value = post.example)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReactionButton(label = if (liked) "Loved" else "Like", icon = "L", count = post.likes + if (liked) 1 else 0, selected = liked) {
                            liked = !liked
                            if (liked) disliked = false
                            scope.launch {
                                scale.snapTo(0.92f)
                                scale.animateTo(1f, tween(160))
                            }
                        }
                        ReactionButton(label = "No", icon = "D", count = post.dislikes + if (disliked) 1 else 0, selected = disliked) {
                            disliked = !disliked
                            if (disliked) liked = false
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MiniAction("C", post.comments.toString())
                        MiniAction(if (saved) "S" else "B", (post.saves + if (saved) 1 else 0).toString()) { saved = !saved }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeedbackChip("Helpful", post.helpful, Mint)
                    FeedbackChip("Incorrect", post.incorrect, Color(0xFFFFE3E6))
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (expanded) "Show less" else "More details",
                        color = Purple,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                }
            }
            AnimatedVisibility(
                visible = pulse,
                enter = scaleIn(initialScale = 0.35f) + fadeIn(),
                exit = scaleOut(targetScale = 1.4f) + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .scale(scale.value)
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(AccentBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LIKE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun PostAuthor(post: WordPost) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(initials = post.initials, colors = post.avatarColors, size = 44)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(post.username, color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("shared a word card", color = Muted, fontSize = 12.sp)
        }
        EngagementPill("3m", Lilac, Purple)
    }
}

@Composable
private fun Avatar(initials: String, colors: List<Color>, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = Color.White, fontWeight = FontWeight.Black, fontSize = (size / 2.5).sp)
    }
}

@Composable
private fun ChipSection(title: String, chips: List<String>, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = Muted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chips) { chip ->
                EngagementPill(text = chip, background = color, color = Ink)
            }
        }
    }
}

@Composable
private fun DetailBlock(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF6F2FF))
            .padding(14.dp)
    ) {
        Text(label, color = Purple, fontWeight = FontWeight.Black, fontSize = 12.sp)
        Text(value, color = Ink, fontSize = 15.sp, lineHeight = 21.sp)
    }
}

@Composable
private fun ReactionButton(label: String, icon: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) AccentBrush else Brush.linearGradient(listOf(Color(0xFFF7F3FF), Color(0xFFFFF6EF))))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(icon, color = if (selected) Color.White else Purple, fontWeight = FontWeight.Black, fontSize = 12.sp)
        Text("$count", color = if (selected) Color.White else Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(label, color = if (selected) Color.White else Muted, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun MiniAction(icon: String, value: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .border(1.dp, Color(0xFFECE2F9), RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(icon, color = Purple, fontWeight = FontWeight.Black, fontSize = 12.sp)
        Text(value, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun FeedbackChip(label: String, count: Int, color: Color) {
    EngagementPill(text = "$label $count", background = color, color = Ink)
}

@Composable
private fun EngagementPill(text: String, background: Color, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
private fun ProfileScreen() {
    val posts = remember { samplePosts }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(18.dp, 18.dp, 18.dp, 104.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            ProfileHero()
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile("Followers", "12.8K", Modifier.weight(1f))
                StatTile("Following", "483", Modifier.weight(1f))
                StatTile("Posts", "156", Modifier.weight(1f))
            }
        }
        item {
            Text("Shared Words", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                userScrollEnabled = false,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(520.dp)
            ) {
                items(posts) { post ->
                    ProfilePostTile(post)
                }
            }
        }
    }
}

@Composable
private fun ProfileHero() {
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.94f), shadowElevation = 4.dp) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Avatar("AV", listOf(Purple, Blue, Orange), 86)
            Text("@ava.words", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(
                "Collecting sharp little words for curious minds. New etymology notes every evening.",
                color = Muted,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .background(AccentBrush, RoundedCornerShape(18.dp))
            ) {
                Text("Follow", color = Color.White, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.95f))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Black, maxLines = 1)
        Text(label, color = Muted, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun ProfilePostTile(post: WordPost) {
    Column(
        modifier = Modifier
            .aspectRatio(0.86f)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(Color.White, Sky, Lilac)))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(post.word, color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(post.meaning, color = Muted, fontSize = 13.sp, lineHeight = 18.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            EngagementPill("L ${post.likes}", Mint, Color(0xFF23845A))
            EngagementPill("C ${post.comments}", Color(0xFFFFEBD5), Color(0xFFB45E00))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPostScreen() {
    var word by rememberSaveable { mutableStateOf("") }
    var meaning by rememberSaveable { mutableStateOf("") }
    var synonyms by rememberSaveable { mutableStateOf("") }
    var antonyms by rememberSaveable { mutableStateOf("") }
    var example by rememberSaveable { mutableStateOf("") }
    val previewPost = WordPost(
        id = 99,
        word = word.ifBlank { "Serendipity" },
        meaning = meaning.ifBlank { "A happy discovery made by chance, especially when you were looking for something else." },
        synonyms = synonyms.split(",").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf("chance", "fortune", "fluke") },
        antonyms = antonyms.split(",").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf("misfortune", "design") },
        example = example.ifBlank { "Finding that tiny cafe became the trip's sweetest serendipity." },
        username = "@you",
        initials = "YO",
        avatarColors = listOf(Blue, Purple),
        likes = 0,
        dislikes = 0,
        comments = 0,
        saves = 0,
        helpful = 0,
        incorrect = 0
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Add a Word", color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Text("Build a quick learning card with a required word and meaning.", color = Muted)
        PlazaTextField("Word", "e.g. mellifluous", word, { word = it }, required = true)
        PlazaTextField("Meaning", "What should people remember?", meaning, { meaning = it }, required = true, minLines = 3)
        PlazaTextField("Synonyms", "Comma separated", synonyms, { synonyms = it })
        PlazaTextField("Antonyms", "Comma separated", antonyms, { antonyms = it })
        PlazaTextField("Example", "Use it in a sentence", example, { example = it }, minLines = 2)
        Button(
            onClick = {},
            enabled = word.isNotBlank() && meaning.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .height(54.dp)
                .fillMaxWidth()
                .background(
                    if (word.isNotBlank() && meaning.isNotBlank()) AccentBrush else Brush.linearGradient(listOf(Color(0xFFE3DDEB), Color(0xFFE9E6EE))),
                    RoundedCornerShape(18.dp)
                )
        ) {
            Text("Publish Word", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Text("Live Preview", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
        WordPostCard(previewPost)
    }
}

@Composable
private fun PlazaTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    required: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(18.dp),
        label = { Text(if (required) "$label *" else label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(Lilac, Sky))),
                contentAlignment = Alignment.Center
            ) {
                Text(label.first().toString(), color = Purple, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Purple,
            unfocusedBorderColor = Color(0xFFE8DFF1),
            focusedContainerColor = Color.White.copy(alpha = 0.94f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.92f),
            cursorColor = Purple
        )
    )
}

private val AccentBrush = Brush.linearGradient(listOf(Purple, Blue, Orange))
private val BackgroundBrush = Brush.verticalGradient(
    listOf(
        Color(0xFFFFF8F3),
        Color(0xFFF8F2FF),
        Color(0xFFEFFAFF)
    )
)

private val samplePosts = listOf(
    WordPost(
        id = 1,
        word = "Mellifluous",
        meaning = "Sweet, smooth, and pleasant to hear, especially a voice or piece of music.",
        synonyms = listOf("dulcet", "honeyed", "melodious"),
        antonyms = listOf("harsh", "grating", "raspy"),
        example = "Her mellifluous narration made the old poem feel newly alive.",
        username = "@lexi.lane",
        initials = "LL",
        avatarColors = listOf(Purple, Blue),
        likes = 1420,
        dislikes = 18,
        comments = 96,
        saves = 512,
        helpful = 221,
        incorrect = 3
    ),
    WordPost(
        id = 2,
        word = "Ebullient",
        meaning = "Cheerful and full of energetic enthusiasm.",
        synonyms = listOf("exuberant", "buoyant", "vivacious"),
        antonyms = listOf("flat", "apathetic", "subdued"),
        example = "The team was ebullient after solving the problem before lunch.",
        username = "@wordsmith_mira",
        initials = "WM",
        avatarColors = listOf(Orange, Purple),
        likes = 987,
        dislikes = 9,
        comments = 44,
        saves = 308,
        helpful = 149,
        incorrect = 1
    ),
    WordPost(
        id = 3,
        word = "Liminal",
        meaning = "Relating to a threshold or transitional state between two stages or places.",
        synonyms = listOf("transitional", "in-between", "threshold"),
        antonyms = listOf("settled", "fixed", "definite"),
        example = "Airports have a liminal feeling, as if everyone is briefly between lives.",
        username = "@study.sparks",
        initials = "SS",
        avatarColors = listOf(Blue, Color(0xFF55D6BE)),
        likes = 2110,
        dislikes = 22,
        comments = 118,
        saves = 880,
        helpful = 302,
        incorrect = 5
    ),
    WordPost(
        id = 4,
        word = "Perspicacious",
        meaning = "Having keen insight and a sharp ability to notice and understand things.",
        synonyms = listOf("astute", "discerning", "perceptive"),
        antonyms = listOf("obtuse", "unobservant", "naive"),
        example = "His perspicacious question changed the direction of the whole debate.",
        username = "@daily.diction",
        initials = "DD",
        avatarColors = listOf(Color(0xFF55D6BE), Orange),
        likes = 765,
        dislikes = 7,
        comments = 31,
        saves = 260,
        helpful = 118,
        incorrect = 2
    )
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WordPlazaPreview() {
    AppTheme() {
         WordPlazaApp()
     }
}
