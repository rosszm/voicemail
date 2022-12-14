package dev.zacharyross.voicemail.ui.inbox

import androidx.compose.animation.*
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import dev.zacharyross.voicemail.ui.appDestination
import dev.zacharyross.voicemail.ui.destinations.VoicemailScreenDestination


@OptIn(ExperimentalAnimationApi::class)
object InboxTransitions : DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            VoicemailScreenDestination ->
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left)
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            VoicemailScreenDestination ->
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left).plus(fadeOut())
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            VoicemailScreenDestination ->
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right)
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            VoicemailScreenDestination ->
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right).plus(fadeOut())
            else -> null
        }
    }
}