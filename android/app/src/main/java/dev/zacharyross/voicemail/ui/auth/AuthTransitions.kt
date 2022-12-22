package dev.zacharyross.voicemail.ui.auth

import androidx.compose.animation.*
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import dev.zacharyross.voicemail.ui.appDestination

@OptIn(ExperimentalAnimationApi::class)
object AuthTransitions : DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            else -> fadeIn().plus(scaleIn(initialScale = .5f))
        }
    }
    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            else -> scaleOut(targetScale = .5f).plus(fadeOut())
        }
    }
    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            else -> fadeIn().plus(scaleIn(initialScale = .5f))
        }
    }
    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            else -> scaleOut(targetScale = .5f).plus(fadeOut())
        }
    }
}