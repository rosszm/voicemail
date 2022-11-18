package dev.zacharyross.voicemail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import dev.zacharyross.voicemail.ui.NavGraphs
import dev.zacharyross.voicemail.ui.destinations.InboxScreenDestination
import dev.zacharyross.voicemail.ui.destinations.VoicemailScreenDestination
import dev.zacharyross.voicemail.ui.inbox.InboxViewModel
import dev.zacharyross.voicemail.ui.theme.VoicemailTheme
import dev.zacharyross.voicemail.ui.voicemail.VoicemailViewModel
import com.ramcosta.composedestinations.navigation.dependency as dependency


/**
 * The main activity of the voicemail application.
 *
 * This activity acts as an entrypoint to the compose application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoicemailTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostEngine = rememberAnimatedNavHostEngine()
                    val navController = navHostEngine.rememberNavController()
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        engine = navHostEngine,
                        navController = navController,
                    )
                }
            }
        }
    }
}
