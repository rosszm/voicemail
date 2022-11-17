package dev.zacharyross.voicemail.ui.voicemail.button

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneCallback
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel


/**
 * The send text button for the voicemail app. When clicked, this button will open a new SMS
 * activity where the SMS recipient is the sender of the voicemail.
 */
@Composable
fun SendTextButton(voicemail: VoicemailUiModel) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + voicemail.fromNumber))
            context.startActivity(intent)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Outlined.Sms, contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(R.string.send_text)
            )
        }
    }
}

/**
 * The call back button for the voicemail app. When clicked, this button will open a new phone
 * activity. If the user has granted phone permissions, the button will open a call directly,
 * otherwise, a call dial activity will be opened.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallBackButton(voicemail: VoicemailUiModel) {
    val context = LocalContext.current

    val startCallActivity = {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + voicemail.fromNumber))
        context.startActivity(intent)
    }
    val startDialActivity = {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + voicemail.fromNumber))
        context.startActivity(intent)
    }

    var phonePermissionAlreadyRequested by rememberSaveable { mutableStateOf(false) }
    val phonePermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE) { granted ->
        phonePermissionAlreadyRequested = true

        if (granted)
            startCallActivity()
        else
            startDialActivity()
    }

    OutlinedButton(
        onClick = {
            when (phonePermissionState.status) {
                PermissionStatus.Granted -> startCallActivity()
                is PermissionStatus.Denied -> {
                    if (!phonePermissionAlreadyRequested)
                        phonePermissionState.launchPermissionRequest()
                    else
                        startDialActivity()
                }
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Outlined.PhoneCallback, contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(R.string.call_back)
            )
        }
    }
}