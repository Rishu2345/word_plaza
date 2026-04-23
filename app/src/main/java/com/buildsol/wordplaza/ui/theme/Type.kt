package org.example.notable.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.buildsol.wordplaza.R


@Composable
fun bodyFontFamily(): FontFamily {
    return FontFamily(
        Font(R.font.inter_light, weight = FontWeight.Light),
        Font(R.font.inter_18pt_regular, weight = FontWeight.Normal),
        Font(R.font.inter_18pt_medium, weight = FontWeight.Medium),
        Font(R.font.inter_18pt_semi_bold, weight = FontWeight.SemiBold),
        Font(R.font.inter_bold, weight = FontWeight.Bold)
    )
}

@Composable
fun displayFontFamily(): FontFamily {
    return FontFamily(
        Font(R.font.roboto_flex_variable_font)
    )
}



@Composable
fun scaledTypography(scale: Float): Typography {

    val base = Typography()

    return Typography(
        displayLarge = base.displayLarge.copy(
            fontFamily = displayFontFamily(),
            fontSize = base.displayLarge.fontSize * scale
        ),
        displayMedium = base.displayMedium.copy(
            fontFamily = displayFontFamily(),
            fontSize = base.displayMedium.fontSize * scale
        ),
        displaySmall = base.displaySmall.copy(
            fontFamily = displayFontFamily(),
            fontSize = base.displaySmall.fontSize * scale
        ),

        headlineLarge = base.headlineLarge.copy(
            fontFamily = displayFontFamily(),
            fontSize = base.headlineLarge.fontSize * scale
        ),
        headlineMedium = base.headlineMedium.copy(
            fontFamily = displayFontFamily(),
            fontSize = base.headlineMedium.fontSize * scale
        ),
        headlineSmall = base.headlineSmall.copy(
            fontFamily = displayFontFamily(),
            fontSize = base.headlineSmall.fontSize * scale
        ),

        titleLarge = base.titleLarge.copy(
            fontFamily = displayFontFamily(),
            fontSize = base.titleLarge.fontSize * scale
        ),
        titleMedium =
            base.titleMedium.copy(
                fontFamily = displayFontFamily(),
                fontSize = base.titleMedium.fontSize * scale
            ),
        titleSmall = base.titleSmall.copy(
            fontFamily = displayFontFamily(),
            fontSize = base.titleSmall.fontSize * scale
        ),


        bodyLarge = base.bodyLarge.copy(
            fontFamily = bodyFontFamily(),
            fontSize = base.bodyLarge.fontSize * scale
        ),

        bodyMedium = base.bodyMedium.copy(
            fontFamily = bodyFontFamily(),
            fontSize = base.bodyMedium.fontSize * scale
        ),

        bodySmall = base.bodySmall.copy(
            fontFamily = bodyFontFamily(),
            fontSize = base.bodySmall.fontSize * scale

        ),


        labelLarge = base.labelLarge.copy(
            fontFamily = bodyFontFamily(),
            fontSize = base.labelLarge.fontSize * scale
        ),

        labelMedium = base.labelMedium.copy(
            fontFamily = bodyFontFamily(),
            fontSize = base.labelMedium.fontSize * scale
        ),
        labelSmall = base.labelSmall.copy(
            fontFamily = bodyFontFamily(),
            fontSize = base.labelSmall.fontSize * scale
        ),
    )
}




