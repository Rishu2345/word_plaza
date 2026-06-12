package com.buildsol.wordplaza.images

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.buildsol.wordplaza.R

enum class Image {
    FIRST,
    SECOND,
    THREE


}
//
@Composable
fun imagePainter(image: Image): Painter{
    return when(image){
            Image.FIRST -> painterResource(R.drawable.onboarding_first)
            Image.SECOND -> painterResource(R.drawable.onboarding_second)
            Image.THREE -> painterResource(R.drawable.onboarding_forth)


    }
}