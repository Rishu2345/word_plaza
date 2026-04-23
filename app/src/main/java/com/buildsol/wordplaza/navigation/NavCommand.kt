package com.buildsol.wordplaza.navigation

import org.example.notable.components.systemTimeInMillis
import kotlin.reflect.KClass



sealed class NavCommand(
    val timestamp: Long = systemTimeInMillis(),
) {
    class Navigate(
        val destination: GTCAppRoute,
        val clearBackStack: Boolean = false,
        val popUpTo: GTCAppRoute? = null,
        val popUpToClass: KClass<*>? = null,
        val popUpToInclusive: Boolean = false,
        timestamp: Long = systemTimeInMillis(),
    ) : NavCommand(timestamp)

    class PopToRoute(
        val destination: GTCAppRoute,
        val inclusive: Boolean,
        timestamp: Long = systemTimeInMillis(),
    ): NavCommand(timestamp)

    class PopToRouteClass(
        val destination: KClass<*>,
        val inclusive: Boolean,
        timestamp: Long = systemTimeInMillis(),
    ): NavCommand(timestamp)

    class PopUp(
        timestamp: Long = systemTimeInMillis(),
    ): NavCommand(timestamp)


}