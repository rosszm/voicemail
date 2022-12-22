package dev.zacharyross.voicemail.ui.inbox.topbar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.common.BackButton
import dev.zacharyross.voicemail.ui.common.fields.SearchField
import dev.zacharyross.voicemail.ui.common.fields.SearchFieldDisabledIcons
import dev.zacharyross.voicemail.ui.inbox.InboxItem
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.common.interaction.NoRippleInteractionSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxTopSearchBar(
    modifier: Modifier = Modifier,
    search: (it: String) -> Flow<List<VoicemailUiModel>>,
    onSignOutClick: () -> Unit = {},
    onResultClick: (it: String) -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    val noRippleInteractionSource = remember { NoRippleInteractionSource() }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val expandedState = remember { MutableTransitionState(false) }
    val isExpanded by remember { derivedStateOf { expandedState.currentState } }
    var query by remember { mutableStateOf("") }

    val expandScale by animateFloatAsState(
        animationSpec = if (expandedState.targetState)
            tween(20, easing = EaseOutCubic) else tween(200, easing = EaseInCubic),
        targetValue = if (!expandedState.targetState) 0f else 1f)

    val searchBarCorners by remember { derivedStateOf { ((1f - expandScale) * 50).toInt() } }

    val surfacePaddingValues  by remember { derivedStateOf {
        PaddingValues(
            vertical = ((1 - expandScale) * 8).dp,
            horizontal = ((1 - expandScale) * 12).dp)
    }}

    val surfaceShadow by remember { derivedStateOf { (3 + (expandScale * 3)).dp } }

    val systemBarsColor by animateColorAsState(
        animationSpec = tween(20, easing = EaseOutExpo),
        targetValue = if (!expandedState.targetState)
                MaterialTheme.colorScheme.background
            else
                MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
    )

    LaunchedEffect(systemBarsColor) {
        systemUiController.setStatusBarColor(systemBarsColor)
        if (expandedState.targetState) delay (50)
        systemUiController.setNavigationBarColor(systemBarsColor)
    }

    LaunchedEffect(expandedState.currentState) {
        if (expandedState.currentState)
            focusRequester.requestFocus()
    }

    val closeSearch = remember {{
        query = ""
        expandedState.targetState = false
        focusManager.clearFocus()
    }}

    BackHandler(isExpanded) { closeSearch() }

    Surface(
        shape = RoundedCornerShape(searchBarCorners),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .padding(surfacePaddingValues)
            .shadow(surfaceShadow, RoundedCornerShape(searchBarCorners)),
        onClick = { if (!isExpanded) expandedState.targetState = true else focusManager.clearFocus() },
        interactionSource = noRippleInteractionSource
    ) {
        Column {
            SearchField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(vertical = if (expandedState.targetState) 8.dp else 0.dp),
                value = query,
                onValueChange = { query = it },
                onSearch = { focusManager.clearFocus() },
                enabled = expandedState.targetState,
                placeholder = stringResource(R.string.search_messages),
                actionIcon = {
                    ClearButton(
                        visible = query.isNotBlank(),
                        onClick = { query = "" })
                },
                disabledIcons = SearchFieldDisabledIcons(
                    leadingIcon = { BackButton(onClick = closeSearch) },
                    trailingIcon = { SettingsButton(onSignOutClick) }
                )
            )

            AnimatedVisibility(
                visibleState = expandedState,
                enter = expandIn(tween(500, easing = EaseInOutCubic)),
                exit = shrinkOut(tween(200, easing = EaseInOutCubic)),

            ) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                SearchResults(
                    value = query,
                    search = search,
                    onResultClick = onResultClick
                )
            }
        }
    }
}

@Composable
fun ClearButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    if (visible)
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.clear_search),
                tint = MaterialTheme.colorScheme.onSurface)
        }
}

@Composable
fun SearchResults(
    value: String,
    search: (String) -> Flow<List<VoicemailUiModel>>,
    onResultClick: (String) -> Unit,
) {
    val results = search(value).collectAsState(initial = listOf())

    LazyColumn(
        modifier = Modifier.fillMaxHeight()
    ) {
        items(
            items = results.value,
            key = { voicemail -> voicemail.id }
        ) { voicemail ->
            InboxItem(
                voicemail = voicemail,
                onClick = { onResultClick(voicemail.id) }
            )
        }
    }
}