package dev.zacharyross.voicemail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.spec.NavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import dev.zacharyross.voicemail.ui.NavGraphs
import dev.zacharyross.voicemail.ui.auth.AuthViewModel
import dev.zacharyross.voicemail.ui.inbox.InboxViewModel
import dev.zacharyross.voicemail.ui.theme.VoicemailTheme
import com.ramcosta.composedestinations.navigation.dependency as dependency


/**
 * The main activity of the voicemail application.
 *
 * This activity acts as an entrypoint to the compose application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var navController: NavHostController? = null

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            VoicemailTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostEngine = rememberAnimatedNavHostEngine()
                    navController = navHostEngine.rememberNavController()

                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        engine = navHostEngine,
                        navController = navController!!,
                        dependenciesContainerBuilder = {
                            dependency(NavGraphs.auth) {
                                val parentEntry = remember(navBackStackEntry) {
                                    navController.getBackStackEntry(NavGraphs.auth.route)
                                }
                                hiltViewModel<AuthViewModel>(parentEntry)
                            }
                            dependency(NavGraphs.inbox) {
                                val parentEntry = remember(navBackStackEntry) {
                                    navController.getBackStackEntry(NavGraphs.inbox.route)
                                }
                                hiltViewModel<InboxViewModel>(parentEntry)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        if (uri != null)
            navController?.navigate(uri)
    }
}
