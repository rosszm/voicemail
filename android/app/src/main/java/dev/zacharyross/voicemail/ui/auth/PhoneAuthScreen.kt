package dev.zacharyross.voicemail.ui.auth

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.NavGraphs
import dev.zacharyross.voicemail.ui.auth.pages.PhoneEnterCodePage
import dev.zacharyross.voicemail.ui.auth.pages.PhoneSendCodePage
import dev.zacharyross.voicemail.ui.destinations.PhoneAuthScreenDestination
import dev.zacharyross.voicemail.ui.common.BackButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@AuthNavGraph(start = true)
@Destination(style = AuthTransitions::class)
@Composable
fun PhoneAuthScreen(
    navigator: DestinationsNavigator,
    viewModel: AuthViewModel,
    prevRoute: String?,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val isStartPage by remember { derivedStateOf{ pagerState.currentPage == 0 } }

    LaunchedEffect(Unit) {
        while (viewModel.authStateFlow == null)
            delay(10)
        viewModel.authStateFlow?.collectLatest {
            if (it != null)
                navigator.navigate(prevRoute ?: NavGraphs.root.route) {
                    popUpTo(PhoneAuthScreenDestination.route) {
                        inclusive = true
                    }
                }
        }
    }

    BackHandler(!isStartPage) { navigatePageBack(coroutineScope, pagerState) }

    val pagerDestinations = rememberSaveable {
        listOf<@Composable () -> Unit>(
            { PhoneSendCodePage(
                phoneNumberStateFlow = viewModel.phoneNumberStateFlow,
                focusManager = focusManager,
                navigateNext = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                sendCode = { viewModel.verifyPhoneNumber(context as Activity) }
            ) },
            { PhoneEnterCodePage(
                authEventFlow = viewModel.authEventFlow,
                verifyCode = { viewModel.signInWithSmsVerificationCode(it) }
            ) }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        activeColor = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = { if (!isStartPage)
                    BackButton(stringResource(id = R.string.back) ) { navigatePageBack(coroutineScope, pagerState) }
                })
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {

            HorizontalPager(
                userScrollEnabled = false,
                count = pagerDestinations.size,
                state = pagerState,
            ) { currentPage ->
                pagerDestinations[currentPage]()
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
fun navigatePageBack(scope: CoroutineScope, pagerState: PagerState) {
    scope.launch {
        pagerState.animateScrollToPage(pagerState.currentPage - 1)
    }
}