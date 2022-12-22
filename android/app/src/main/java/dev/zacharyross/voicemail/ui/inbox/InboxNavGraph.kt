package dev.zacharyross.voicemail.ui.inbox

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph


@RootNavGraph(start = true)
@NavGraph
annotation class InboxNavGraph(
    val start: Boolean = false
)
