package org.dhis2.commons.dialogs

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.dhis2.commons.R

const val EMPTY_STRING = " "

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun PreviewDialog() {
    CustomDialog(value = "Value", setShowDialog = { true }, setValue = { "Value" })
}

@Composable
fun NewDialog() {
    val painter = painterResource(id = R.drawable.dialog_icon)
    AlertDialog(
        title = {
            Column {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "title",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = "message",
                fontSize = 16.sp,
            )
        },
        confirmButton = {
            Text(
                text = "Text",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        dismissButton = {
            Text(
                text = "Text",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        onDismissRequest = { },
        modifier = Modifier.fillMaxHeight()
    )
}

fun programDialog(context: Context, okClicked: () -> Unit = {}) {
    MaterialAlertDialogBuilder(context)
        .setTitle("Title")
        .setMessage("Message")
        .setPositiveButton("Ok") { _, _ ->
            okClicked.invoke()
        }
        .setCancelable(false)
        .show()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomDialog(value: String, setShowDialog: (Boolean) -> Unit, setValue: (String) -> Unit) {

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DialogIcon()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tem local para lavar as mãos",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pode ser uma torneira com água canalizada ou recepientes",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = colorResource(id = R.color.colorPrimary_f57)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        item {
                            MediaItem(
                                type = MediaType.VIDEO,
                                duration = "01:10 minutos",
                                dateOfLastUpdate = "01-01-2021"
                            )
                        }
                        item {
                            MediaItem(
                                type = MediaType.AUDIO,
                                duration = "12:14 minutos",
                                dateOfLastUpdate = "11-12-2022"
                            )
                        }
                    }

                    ButtonClose(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun ButtonClose(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(0.5.dp, colorResource(R.color.colorPrimary_b0b)),
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text = "Fechar",
            fontSize = 12.sp,
            color = colorResource(R.color.textPrimary)
        )
    }
}

@Composable
private fun MediaData(
    duration: String,
    dateOfLastUpdate: String,
) {
    Column {
        Text(
            text = "Exemplos",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Row {
            Text(
                text = "Duração:$EMPTY_STRING",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = duration,
                fontSize = 12.sp,
            )
        }
        Row {
            Text(
                text = "Atualização:$EMPTY_STRING",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = dateOfLastUpdate,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun DialogItemIcon(
    type: MediaType,
    size: Dp,
) {
    val drawableRes = if (MediaType.VIDEO == type) {
        R.drawable.dialog_video
    } else {
        R.drawable.dialog_audio
    }
    Image(
        painter = painterResource(drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = Modifier.size(size)
    )
}

@Composable
private fun DialogIcon() {
    Image(
        painter = painterResource(R.drawable.dialog_icon),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = Modifier.size(72.dp)
    )
}

@Composable
private fun MediaItem(
    type: MediaType,
    duration: String,
    dateOfLastUpdate: String,
) {
    Column(Modifier.padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = colorResource(R.color.colorPrimary_b0b),
                    shape = RoundedCornerShape(1)
                )
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 4.dp, top = 4.dp, end = 8.dp)
        ) {
            DialogItemIcon(
                type = type,
                size = 56.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            MediaData(
                duration = duration,
                dateOfLastUpdate = dateOfLastUpdate
            )
        }
    }
}

enum class MediaType {
    VIDEO,
    AUDIO,
}
