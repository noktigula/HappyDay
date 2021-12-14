package com.happyday.android.alarmslist

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.happyday.android.R

@Composable
fun OverlayPermissionDialog(onClick: ()->Unit) {
    AlertDialog(
        onDismissRequest = {
          /*Do nothing*/
        },
        title = {
            Text(text = stringResource(id = R.string.overlay_permission_title))
        },
        text = {
            Text(text = stringResource(id = R.string.overlay_permission_body))
        },
        buttons = {
            Button(
                onClick = onClick
            ) {
                Text(text = stringResource(id = R.string.overlay_permission_button))
            }
        }
    )
}