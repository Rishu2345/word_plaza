package org.example.notable.images

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

enum class Image {
    DARK_MODE_MOON,
    ADD,


}

@Composable
fun imagePainter(image: Image): Painter{
    return when(image){


    }
}