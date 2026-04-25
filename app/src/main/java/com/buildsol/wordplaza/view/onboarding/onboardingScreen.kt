package com.buildsol.wordplaza.view.onboarding

/*
 * Copyright 2026 Kyriakos Georgiopoulos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.buildsol.wordplaza.R
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin

private val BUTTON_SIZE = 80.dp
private val BOTTOM_PADDING = 140.dp
private const val SPLIT_POINT = 0.65f
private const val VISIBILITY_START = 0.2f
private val CONTENT_BOTTOM_PADDING = BOTTOM_PADDING + BUTTON_SIZE

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreens(onFinished: () -> Unit = {}) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })

    var screenWidthPx by remember { mutableFloatStateOf(0f) }
    var screenHeightPx by remember { mutableFloatStateOf(0f) }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                screenWidthPx = size.width.toFloat()
                screenHeightPx = size.height.toFloat()
            }
    ) { page ->
        val item = onboardingPages[page]

        val isOverlay by remember(page) {
            derivedStateOf { page != pagerState.settledPage }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(if (isOverlay) 1f else 0f)
                .graphicsLayer {
                    translationX = pagerState.offsetForPage(page) * size.width
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .liquidSwipeClip(
                        pagerState = pagerState,
                        page = page,
                        buttonSizeDp = BUTTON_SIZE,
                        bottomPaddingDp = BOTTOM_PADDING
                    )
            ) {
                SlidingBackground(pagerState = pagerState, page = page, bgColor = item.bgColor)
                SlidingContent(pagerState = pagerState, page = page, item = item)
            }

            LiquidSwipeFab(
                pagerState = pagerState,
                page = page,
                item = item,
                screenWidthPx = screenWidthPx,
                screenHeightPx = screenHeightPx,
                onFinished = onFinished
            )
        }
    }
}

/**
 * Background that slides with an eased counter-translation to create depth.
 *
 * The raw page offset ([-1..1]) is inverted and run through [FastOutSlowInEasing]
 * to produce a non-linear lag. The background translates at 42% of the screen
 * width in the opposite direction of the swipe, so it appears to "stick" slightly
 * behind the page content, producing a subtle parallax depth layer.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SlidingBackground(pagerState: PagerState, page: Int, bgColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .graphicsLayer {
                val pageOffset = pagerState.offsetForPage(page)
                val p = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                val t = FastOutSlowInEasing.transform(p)

                translationX = if (pageOffset <= 0f) {
                    size.width * (1f - t) * 0.42f
                } else {
                    -size.width * (1f - t) * 0.42f
                }
            }
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SlidingContent(pagerState: PagerState, page: Int, item: OnboardingPage) {
    val titleStyle = remember(item.textGradient) {
        TextStyle(
            brush = item.textGradient,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = CONTENT_BOTTOM_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        /**
         * Image transition: "tossed object" physics.
         *
         * Four simultaneous transforms run inside a single [graphicsLayer]:
         *   1. **Scale decay**: shrinks to 70% at full offset via `1 - 0.3 * progress`.
         *   2. **Rotational twist**: up to 25 degrees proportional to swipe direction,
         *      simulating a flicked card spinning away from the finger.
         *   3. **Damped parallax**: translates at 15% of its width, multiplied by
         *      a `(1 - progress)` decay so the drift returns to zero before the
         *      image reaches the liquid-swipe clip boundary.
         *   4. **Alpha fade**: drops to 20% opacity at full offset.
         */
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(220.dp)
                .animatedStarEffect()
                .graphicsLayer {
                    val pageOffset = pagerState.offsetForPage(page)
                    val progress = pageOffset.absoluteValue.coerceIn(0f, 1f)

                    val imageScale = 1f - (progress * 0.3f)
                    scaleX = imageScale
                    scaleY = imageScale
                    rotationZ = pageOffset * 25f

                    val fadeOut = 1f - progress
                    translationX = pageOffset * size.width * 0.15f * fadeOut

                    alpha = 1f - (progress * 0.8f)
                }
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(40.dp))

        /**
         * Text transition: "falling glass pane" physics.
         *
         *   1. **Counter-parallax**: translates at 60% of width in the OPPOSITE
         *      direction of the swipe, creating the illusion of a layer closer
         *      to the viewer than the image behind it.
         *   2. **Gravity drop**: `translationY` increases by 120px, simulating
         *      the text sliding downward as if losing support.
         *   3. **Perspective tilt**: `rotationX` reaches -45 degrees, tilting
         *      the text backward on the X-axis like a card falling away.
         *   4. **Threshold fade**: alpha uses a shifted normalization
         *      `(1 - progress - 0.2) / 0.8` so the text fully disappears
         *      before the page is 80% swiped, avoiding ghosting artifacts.
         */
        Text(
            text = item.title,
            style = titleStyle,
            modifier = Modifier
                .animatedShineEffect()
                .graphicsLayer {
                    val pageOffset = pagerState.offsetForPage(page)
                    val progress = pageOffset.absoluteValue.coerceIn(0f, 1f)

                    val textScale = 1f - (progress * 0.15f)
                    scaleX = textScale
                    scaleY = textScale
                    translationX = -pageOffset * size.width * 0.6f
                    translationY = progress * 120f
                    rotationX = progress * -45f
                    alpha =
                        ((1f - progress) - VISIBILITY_START).coerceIn(
                            0f,
                            1f
                        ) / (1f - VISIBILITY_START)
                }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.LiquidSwipeFab(
    pagerState: PagerState,
    page: Int,
    item: OnboardingPage,
    screenWidthPx: Float,
    screenHeightPx: Float,
    onFinished: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val buttonRadiusPx = remember(density) {
        with(density) { (BUTTON_SIZE / 2).toPx() }
    }

    val triggerFill by remember(page) {
        derivedStateOf {
            val isOutgoingFab = page == pagerState.settledPage
            val absoluteOffsetFab = pagerState.offsetForPage(page).absoluteValue
            isOutgoingFab || (!isOutgoingFab && absoluteOffsetFab < 0.20f)
        }
    }

    val fillProgress by animateFloatAsState(
        targetValue = if (triggerFill) 1f else 0f,
        animationSpec = tween(
            durationMillis = 430,
            easing = FastOutSlowInEasing
        ),
        label = "fillProgress"
    )

    val onClickLambda = remember(page, onFinished) {
        {
            coroutineScope.launch {
                if (page < onboardingPages.lastIndex) {
                    pagerState.animateScrollToPage(
                        page = page + 1,
                        animationSpec = spring(
                            dampingRatio = 1.0f,
                            stiffness = 140f
                        )
                    )
                } else {
                    onFinished()
                }
            }
            Unit
        }
    }

    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = BOTTOM_PADDING)
            .size(BUTTON_SIZE)
            .graphicsLayer {
                val isOutgoingFab = page == pagerState.settledPage
                val pageOffsetFab = pagerState.offsetForPage(page)
                val absoluteOffsetFab = pageOffsetFab.absoluteValue
                val masterProgressFab = if (isOutgoingFab) {
                    absoluteOffsetFab.coerceIn(0f, 1f)
                } else {
                    (1f - absoluteOffsetFab).coerceIn(0f, 1f)
                }
                val isSwipingForwardFab = if (isOutgoingFab) {
                    pageOffsetFab > 0f
                } else {
                    pageOffsetFab < 0f
                }

                if (masterProgressFab > 0f && masterProgressFab < 1f) {
                    val horizontalTravel = screenWidthPx * 0.55f
                    val verticalTravel = -screenHeightPx * 0.015f
                    val arcHeight = screenHeightPx * 0.02f

                    val waveRadiusX = screenWidthPx * 0.65f
                    val waveRadiusY = screenHeightPx * 1.5f
                    val maxBaseScale = waveRadiusX / buttonRadiusPx
                    val maxVerticalBonus = (waveRadiusY / buttonRadiusPx) - maxBaseScale

                    if (isOutgoingFab && masterProgressFab <= SPLIT_POINT) {
                        val p = (masterProgressFab / SPLIT_POINT).coerceIn(0f, 1f)
                        applyFabTransform(
                            p = p,
                            horizontalTravel = horizontalTravel,
                            verticalTravel = verticalTravel,
                            arcHeight = arcHeight,
                            isSwipingForward = isSwipingForwardFab,
                            maxBaseScale = maxBaseScale,
                            maxVerticalBonus = maxVerticalBonus,
                            isOutgoing = true
                        )
                    } else if (!isOutgoingFab && masterProgressFab > SPLIT_POINT) {
                        val pReverse =
                            1f - ((masterProgressFab - SPLIT_POINT) / (1f - SPLIT_POINT)).coerceIn(
                                0f,
                                1f
                            )

                        val incomingWaveRadiusX = screenWidthPx - waveRadiusX
                        val incomingMaxBaseScale = incomingWaveRadiusX / buttonRadiusPx
                        val incomingMaxVerticalBonus =
                            (waveRadiusY / buttonRadiusPx) - incomingMaxBaseScale

                        applyFabTransform(
                            p = pReverse,
                            horizontalTravel = horizontalTravel,
                            verticalTravel = verticalTravel,
                            arcHeight = arcHeight,
                            isSwipingForward = isSwipingForwardFab,
                            maxBaseScale = incomingMaxBaseScale,
                            maxVerticalBonus = incomingMaxVerticalBonus,
                            isOutgoing = false
                        )
                    } else {
                        alpha = 0f
                    }
                } else if ((isOutgoingFab && masterProgressFab == 0f) || (!isOutgoingFab && masterProgressFab == 1f)) {
                    alpha = 1f
                    scaleX = 1f
                    scaleY = 1f
                    translationX = 0f
                    translationY = 0f
                } else {
                    alpha = 0f
                }
            }
            .clip(CircleShape)
            .drawBehind {
                val pageOffsetFab = pagerState.offsetForPage(page)
                val dynamicSourceIndex =
                    if (pageOffsetFab < 0f) (page - 1).coerceAtLeast(0)
                    else (page + 1).coerceAtMost(onboardingPages.lastIndex)

                val camoColor = onboardingPages[dynamicSourceIndex].bgColor

                if (fillProgress >= 1f) {
                    drawCircle(color = item.buttonColor)
                } else if (fillProgress <= 0f) {
                    drawCircle(color = camoColor)
                } else {
                    drawCircle(color = camoColor)
                    val currentStroke = (size.width / 2f) * fillProgress
                    drawCircle(
                        color = item.buttonColor,
                        radius = (size.width / 2f) - (currentStroke / 2f),
                        style = Stroke(width = currentStroke)
                    )
                }
            }
            .clickable(onClick = onClickLambda)
    ) {
        FabIcon(pagerState = pagerState, page = page, tintColor = item.bgColor)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.FabIcon(pagerState: PagerState, page: Int, tintColor: Color) {
    val targetScale by remember(page) {
        derivedStateOf {
            val isSettled = page == pagerState.settledPage && !pagerState.isScrollInProgress
            val isOutgoing = page == pagerState.settledPage
            val absOffset = pagerState.offsetForPage(page).absoluteValue
            val masterProgress = if (isOutgoing) {
                absOffset.coerceIn(0f, 1f)
            } else {
                (1f - absOffset).coerceIn(0f, 1f)
            }

            when {
                isSettled -> 1f
                isOutgoing && masterProgress < 0.15f -> 0.7f
                !isOutgoing && masterProgress > 0.85f -> (masterProgress - 0.85f) / 0.15f
                else -> 0f
            }.coerceIn(0f, 1f)
        }
    }

    val iconScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = 0.95f,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    val isFullySettled by remember(page) {
        derivedStateOf {
            page == pagerState.settledPage && !pagerState.isScrollInProgress
        }
    }

    val iconRotation by animateFloatAsState(
        targetValue = if (isFullySettled) 0f else -45f,
        animationSpec = spring(
            dampingRatio = 0.98f,
            stiffness = 180f
        ),
        label = "iconRotation"
    )

    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = "Next",
        tint = tintColor,
        modifier = Modifier
            .align(Alignment.Center)
            .graphicsLayer {
                alpha = if (iconScale > 0.05f) iconScale else 0f
                scaleX = iconScale
                scaleY = iconScale
                rotationZ = iconRotation
            }
    )
}

@OptIn(ExperimentalFoundationApi::class)
private fun PagerState.offsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

/**
 * Moves the FAB along a parabolic arc and morphs it into the liquid-swipe oval.
 *
 * The motion uses two staggered easing curves from a single progress value [p]:
 *
 *   - **tFrac** (`p / 0.82`, clamped): drives position. Reaches its target at 82%
 *     of progress, then holds. Eased through [FastOutSlowInEasing] so the FAB
 *     decelerates at the screen edge like a thrown object with air resistance.
 *
 *   - **eFrac** (`(p - 0.5) / 0.5`, clamped): drives the vertical stretch. Activates
 *     only after 50% progress so the FAB first slides horizontally as a circle,
 *     then elongates into the tall oval that becomes the clip shape.
 *
 * The vertical path is a parametric parabolic arc:
 * ```
 *   y = linearDrift * t  -  arcHeight * sin(pi * t)
 * ```
 * The `sin(pi * t)` term peaks at t=0.5, lifting the FAB above its resting
 * position at the midpoint of horizontal travel, then settling it back down.
 * This produces a natural ballistic trajectory.
 *
 * Scale is anisotropic: `scaleX` grows uniformly to [maxBaseScale] (matching
 * the clip oval's horizontal radius), while `scaleY` adds [maxVerticalBonus]
 * driven by [eFrac], stretching the circle into the tall oval that visually
 * becomes the liquid-swipe reveal shape.
 */
private fun androidx.compose.ui.graphics.GraphicsLayerScope.applyFabTransform(
    p: Float,
    horizontalTravel: Float,
    verticalTravel: Float,
    arcHeight: Float,
    isSwipingForward: Boolean,
    maxBaseScale: Float,
    maxVerticalBonus: Float,
    isOutgoing: Boolean
) {
    val tFrac = (p / 0.82f).coerceIn(0f, 1f)
    val eFrac = ((p - 0.5f) / 0.5f).coerceIn(0f, 1f)

    val easedT = FastOutSlowInEasing.transform(tFrac)
    val easedE = FastOutSlowInEasing.transform(eFrac)

    val currentX = horizontalTravel * easedT
    val arc = sin(easedT * PI).toFloat()
    val currentY = (verticalTravel * easedT) - (arcHeight * arc)

    translationX = if (isSwipingForward) {
        if (isOutgoing) currentX else -currentX
    } else {
        if (isOutgoing) -currentX else currentX
    }
    translationY = currentY

    val baseScale = 1f + (easedT * (maxBaseScale - 1f))
    scaleX = baseScale
    scaleY = baseScale + (easedE * maxVerticalBonus)
    alpha = 1f
}

/**
 * Clips the incoming page to an expanding oval originating from the FAB,
 * producing the liquid-swipe reveal.
 *
 * The animation splits at [SPLIT_POINT] (65%) into two phases:
 *
 * **Phase 1 (0..65%): The oval tracks the FAB.**
 * The clip center follows the same parabolic arc as [applyFabTransform] using
 * identical tFrac/eFrac math, so the oval perfectly overlays the morphing FAB.
 * It grows from button radius to `waveRadiusX * waveRadiusY` (65% screen width
 * by 150% screen height), large enough to reveal a visible "blob" of the
 * incoming page without yet covering the full screen.
 *
 * **Phase 2 (65..100%): The oval explodes to fill the screen.**
 * The center locks at the FAB's final position near the screen edge. Both radii
 * expand toward `2.5 * max(screenWidth, screenHeight)` via [FastOutSlowInEasing],
 * guaranteeing full coverage regardless of aspect ratio. The easing curve makes
 * the final expansion feel like a burst rather than a linear wipe.
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.liquidSwipeClip(
    pagerState: PagerState,
    page: Int,
    buttonSizeDp: Dp,
    bottomPaddingDp: Dp
): Modifier = this.drawWithCache {
    val path = Path()
    val buttonRadiusPx = buttonSizeDp.toPx() / 2f
    val bottomPaddingPx = bottomPaddingDp.toPx()

    onDrawWithContent {
        val isOverlay = page != pagerState.settledPage
        val pageOffset = pagerState.offsetForPage(page)
        val swipeProgress = (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)

        if (isOverlay && swipeProgress > 0f && swipeProgress < 1f) {
            val isFromRight = pageOffset <= 0f

            val horizontalTravel = size.width * 0.55f
            val verticalTravel = -size.height * 0.015f
            val arcHeight = size.height * 0.02f

            val waveRadiusX = size.width * 0.65f
            val waveRadiusY = size.height * 1.5f
            val maxBaseScale = waveRadiusX / buttonRadiusPx
            val maxVerticalBonus = (waveRadiusY / buttonRadiusPx) - maxBaseScale

            val originX = size.width / 2f
            val originY = size.height - bottomPaddingPx - buttonRadiusPx

            val cx: Float
            val cy: Float
            val radiusX: Float
            val radiusY: Float

            if (swipeProgress <= SPLIT_POINT) {
                val p = (swipeProgress / SPLIT_POINT).coerceIn(0f, 1f)
                val tFrac = (p / 0.82f).coerceIn(0f, 1f)
                val eFrac = ((p - 0.5f) / 0.5f).coerceIn(0f, 1f)

                val easedT = FastOutSlowInEasing.transform(tFrac)
                val easedE = FastOutSlowInEasing.transform(eFrac)

                val currentX = horizontalTravel * easedT
                val arc = sin(easedT * PI).toFloat()
                val currentY = (verticalTravel * easedT) - (arcHeight * arc)

                cx = if (isFromRight) originX + currentX else originX - currentX
                cy = originY + currentY

                val baseScaleMultiplier = 1f + (easedT * (maxBaseScale - 1f))
                radiusX = buttonRadiusPx * baseScaleMultiplier
                radiusY = buttonRadiusPx * (baseScaleMultiplier + (easedE * maxVerticalBonus))
            } else {
                val p = ((swipeProgress - SPLIT_POINT) / (1f - SPLIT_POINT)).coerceIn(0f, 1f)
                val easedExp = FastOutSlowInEasing.transform(p)

                cx = if (isFromRight) originX + horizontalTravel else originX - horizontalTravel
                cy = originY + verticalTravel

                val massiveRadius = maxOf(size.width, size.height) * 2.5f

                radiusX = waveRadiusX + (massiveRadius - waveRadiusX) * easedExp
                radiusY = waveRadiusY + (massiveRadius - waveRadiusY) * easedExp
            }

            path.reset()
            path.addOval(
                Rect(
                    left = cx - radiusX,
                    top = cy - radiusY,
                    right = cx + radiusX,
                    bottom = cy + radiusY
                )
            )

            clipPath(path) {
                this@onDrawWithContent.drawContent()
            }
        } else {
            drawContent()
        }
    }
}

/**
 * AGSL modifier: sweeps a diagonal beam of light across text content.
 *
 * The shader projects each fragment onto a tilted 1D axis via `nx + ny * 0.5`,
 * which angles the shine at roughly 27 degrees from vertical. A sweep line
 * moves across this axis over 2.5 seconds. Intensity is a reverse `smoothstep`
 * with 0.15-wide falloff capped at 0.6 peak brightness. The result is added
 * to RGB pre-multiplied by alpha, so the shine only appears on opaque text
 * pixels and leaves transparent background untouched.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Modifier.animatedShineEffect(): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return this
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ShineTransition")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShineTime"
    )

    var widthPx by remember { mutableFloatStateOf(0f) }
    var heightPx by remember { mutableFloatStateOf(0f) }

    val shader = remember { RuntimeShader(SHINE_SHADER) }

    return this
        .onSizeChanged {
            widthPx = it.width.toFloat()
            heightPx = it.height.toFloat()
        }
        .graphicsLayer {
            if (widthPx > 0f && heightPx > 0f) {
                shader.setFloatUniform("size", widthPx, heightPx)
                shader.setFloatUniform("time", time)

                renderEffect = RenderEffect.createRuntimeShaderEffect(
                    shader, "composable"
                ).asComposeRenderEffect()
            }
        }
}

/**
 * AGSL modifier: renders discrete 4-point diamond sparkles over opaque pixels.
 *
 * Each star uses polar-coordinate spokes: `abs(cos(angle * 2))` produces 4 radial
 * arms whose intensity decays linearly with distance (`fade_factor = 40 / sizeFactor`).
 * A small Gaussian core via `smoothstep(0, radius, distance)` adds a bright center dot.
 *
 * The blink effect is `pow(abs(sin(phase)), 8)`: the high exponent narrows the
 * sine peaks so each star is visible for only a tiny fraction of its cycle,
 * producing a sharp glint-and-vanish sparkle. Phase offsets are seeded from
 * `center.x * 20` so stars never blink in sync. The `abs()` around `sin()` is
 * required because AGSL's `pow()` is undefined for negative bases.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Modifier.animatedStarEffect(): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return this
    }

    val infiniteTransition = rememberInfiniteTransition(label = "StarTransition")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "StarTime"
    )

    var widthPx by remember { mutableFloatStateOf(0f) }
    var heightPx by remember { mutableFloatStateOf(0f) }

    val shader = remember { RuntimeShader(STAR_SHINE_SHADER) }

    return this
        .onSizeChanged {
            widthPx = it.width.toFloat()
            heightPx = it.height.toFloat()
        }
        .graphicsLayer {
            if (widthPx > 0f && heightPx > 0f) {
                shader.setFloatUniform("size", widthPx, heightPx)
                shader.setFloatUniform("time", time)

                renderEffect = RenderEffect.createRuntimeShaderEffect(
                    shader, "composable"
                ).asComposeRenderEffect()
            }
        }
}

@Language("AGSL")
private const val SHINE_SHADER = """
    uniform shader composable;
    uniform float2 size;
    uniform float time;
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        if (color.a < 0.05) return color; 
        
        float nx = fragCoord.x / size.x;
        float ny = fragCoord.y / size.y;
        float pos = nx + (ny * 0.5);
        float sweep = (time * 2.5) - 0.5;
        float dist = abs(pos - sweep);
        float shine = smoothstep(0.15, 0.0, dist) * 0.6;
        
        color.rgb = color.rgb + (shine * color.a);
        return clamp(color, 0.0, 1.0);
    }
"""

@Language("AGSL")
private const val STAR_SHINE_SHADER = """
    uniform shader composable;
    uniform float2 size;
    uniform float time;
    
    half4 drawStar(float2 uv, float2 center, float sizeFactor, float intensity, float flickerSpeed, float rotationFactor) {
        float2 distVec = uv - center;
        float distance = length(distVec);
        
        float numSpokes = 4.0; 
        float angle = atan(distVec.y, distVec.x) + (time * rotationFactor);
        float spokes = abs(cos(angle * (numSpokes / 2.0)));
        
        float fade_factor = 40.0 / sizeFactor; 
        float spoke_intensity = max(0.0, spokes - distance * fade_factor);
        
        float core_radius = 0.015 * sizeFactor;
        float core_intensity = 1.0 - smoothstep(0.0, core_radius, distance);
        
        float total_intensity = (spoke_intensity + core_intensity) * intensity;
        
        float phase = (time * flickerSpeed) + (center.x * 20.0);
        float flicker = pow(abs(sin(phase)), 8.0); 
        
        half4 starColor = half4(1.0, 1.0, 1.0, 1.0); 
        starColor.rgb *= (total_intensity * flicker * 2.5);
        
        return clamp(starColor, 0.0, 1.0);
    }
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        if (color.a < 0.1) return color; 
        
        float nx = fragCoord.x / size.x;
        float ny = fragCoord.y / size.y;
        float2 uv = float2(nx, ny);
        
        half4 finalStars = half4(0.0);
        finalStars = max(finalStars, drawStar(uv, float2(0.25, 0.30), 0.5, 0.8, 3.0,  0.5));
        finalStars = max(finalStars, drawStar(uv, float2(0.70, 0.20), 0.4, 0.9, 4.2, -0.6));
        finalStars = max(finalStars, drawStar(uv, float2(0.45, 0.80), 0.6, 0.7, 2.5,  0.7));
        finalStars = max(finalStars, drawStar(uv, float2(0.80, 0.60), 0.3, 1.0, 5.0, -0.4));
        finalStars = max(finalStars, drawStar(uv, float2(0.30, 0.70), 0.5, 0.6, 3.8,  0.8));
        finalStars = max(finalStars, drawStar(uv, float2(0.60, 0.50), 0.4, 0.8, 4.5, -0.9));
        finalStars = max(finalStars, drawStar(uv, float2(0.85, 0.35), 0.5, 0.9, 2.0,  1.0));
        finalStars = max(finalStars, drawStar(uv, float2(0.15, 0.55), 0.3, 0.7, 6.0, -0.5));
        
        color.rgb += (finalStars.rgb * color.a);
        return clamp(color, 0.0, 1.0);
    }
"""

@Stable
data class OnboardingPage(
    val title: String,
    @DrawableRes val imageRes: Int,
    val bgColor: Color,
    val buttonColor: Color,
    val textColor: Color = Color.White,
    val textGradient: Brush
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Local news\nstories",
        imageRes = R.drawable.ic_launcher_foreground,
        bgColor = Color(0xFF1441CC),
        buttonColor = Color(0xFFF19EBC),
        textGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFCE4EC),
                Color(0xFFF19EBC),
                Color(0xFFBA68C8)
            )
        )
    ),
    OnboardingPage(
        title = "Choose your\ninterests",
        imageRes = R.drawable.ic_launcher_foreground,
        bgColor = Color(0xFFF19EBC),
        buttonColor = Color.White,
        textGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF0D47A1),
                Color(0xFF2962FF),
                Color(0xFF00B0FF)
            )
        )
    ),
    OnboardingPage(
        title = "Drag and\ndrop to move",
        imageRes = R.drawable.ic_launcher_foreground,
        bgColor = Color.White,
        buttonColor = Color(0xFF1D7373),
        textColor = Color.Black,
        textGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF1D7373),
                Color(0xFFF49D6A),
            )
        )
    ),
    OnboardingPage(
        title = "Ready to\nexplore",
        imageRes = R.drawable.ic_launcher_foreground,
        bgColor = Color(0xFF1D7373),
        buttonColor = Color(0xFFF49D6A),
        textGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF0E6),
                Color(0xFFF49D6A),
                Color(0xFFFF7043)
            )
        )
    )
)