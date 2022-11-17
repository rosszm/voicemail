package dev.zacharyross.voicemail.ui.voicemail

import androidx.compose.animation.*
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import dev.zacharyross.voicemail.ui.appDestination
import dev.zacharyross.voicemail.ui.destinations.InboxScreenDestination


@OptIn(ExperimentalAnimationApi::class)
object VoicemailTransitions : DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            InboxScreenDestination ->
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left)
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            InboxScreenDestination ->
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            InboxScreenDestination ->
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right)
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {
        return when (targetState.appDestination()) {
            InboxScreenDestination ->
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
            else -> null
        }
    }
}