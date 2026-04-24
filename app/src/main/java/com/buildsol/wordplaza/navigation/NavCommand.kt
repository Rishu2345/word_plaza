package com.buildsol.wordplaza.navigation

import kotlin.reflect.KClass



sealed class NavCommand(
    val timestamp: Long = System.currentTimeMillis(),
) {
    class Navigate(
        val destination: GTCAppRoute,
        val clearBackStack: Boolean = false,
        val popUpTo: GTCAppRoute? = null,
        val popUpToClass: KClass<*>? = null,
        val popUpToInclusive: Boolean = false,
        timestamp: Long = System.currentTimeMillis(),
    ) : NavCommand(timestamp)

    class PopToRoute(
        val destination: GTCAppRoute,
        val inclusive: Boolean,
        timestamp: Long = System.currentTimeMillis(),
    ): NavCommand(timestamp)

    class PopToRouteClass(
        val destination: KClass<*>,
        val inclusive: Boolean,
        timestamp: Long = System.currentTimeMillis(),
    ): NavCommand(timestamp)

    class PopUp(
        timestamp: Long = System.currentTimeMillis(),
    ): NavCommand(timestamp)


}