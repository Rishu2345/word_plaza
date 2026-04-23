package org.example.notable.components


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color

fun Modifier.rippleLoadingAnimationModifier(
    start: Boolean,
    color: Color,
    circles: Int = 3,
    expandFactor: Float = 5f,
    durationMillis: Int = 3500,
): Modifier {
    if (start.not()) {
        return this
    } else {
        return this.composed {
            val transition = rememberInfiniteTransition(label = "ripple")
            val translateAnimations = List(circles) { index ->
                transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = durationMillis,
                            easing = LinearEasing,
                        ),
                        repeatMode = RepeatMode.Restart,
                        initialStartOffset = StartOffset(index * (durationMillis / circles))
                    )
                )
            }

            this.drawBehind {
                val radius = (maxOf(size.height, size.width) / 2)*expandFactor
                translateAnimations.forEachIndexed { index, animatable ->
                    drawCircle(
                        color = color.copy(alpha = (1 - animatable.value)),
                        radius = radius * animatable.value,
                        center = size.center,
                    )
                }
            }
        }
    }
}