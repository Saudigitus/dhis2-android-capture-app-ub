package org.dhis2.commons.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.dhis2.commons.R
import kotlin.random.Random

const val SPACE_STRING = " "

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun PreviewMediaDialog() {
    MediaDialog(
        title = "Tem local para lavar as mãos",
        subTitle = "Pode ser uma torneira com água canalizada ou recepientes",
        listOfAudioUrl = generateRandomURLs(),
        listOfVideosUrl = generateRandomURLs(),
        onMediaItemClicked = {},
        onDismiss = {},
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewVideoMediaItem() {
    MediaDialogItem(
        type = MediaType.VIDEO,
        title = "Como lavar as mãos",
        url = generateRandomURLs()[0],
        duration = generateRandomDuration(),
        dateOfLastUpdate = generateRandomDate()
    ) {}
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewAudioMediaItem() {
    MediaDialogItem(
        type = MediaType.AUDIO,
        title = "Como lavar as mãos",
        url = generateRandomURLs()[0],
        duration = generateRandomDuration(),
        dateOfLastUpdate = generateRandomDate()
    ) {}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MediaDialog(
    title: String,
    subTitle: String,
    listOfVideosUrl: List<String>, //Todo: create and use different object instead of String
    listOfAudioUrl: List<String>, //Todo: create and use different object instead of String
    onMediaItemClicked: (url: String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss.invoke() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MediaDialogMainIcon()
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = subTitle,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = colorResource(id = R.color.colorPrimary_f57)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {

                        for (videoUrl in listOfVideosUrl) {
                            item {
                                MediaDialogItem(
                                    type = MediaType.VIDEO,
                                    title = "Como lavar as mãos", //Todo: use object field instead
                                    url = videoUrl,
                                    duration = generateRandomDuration(), //Todo: use object field instead
                                    onClickMediaItem = { url -> onMediaItemClicked.invoke(url) },
                                    dateOfLastUpdate = generateRandomDate() //Todo: use object field instead
                                )
                            }
                        }

                        for (audioUrl in listOfAudioUrl) {
                            item {
                                MediaDialogItem(
                                    type = MediaType.AUDIO,
                                    title = "Como lavar as mãos", //Todo: use object field instead
                                    url = audioUrl,
                                    duration = generateRandomDuration(), //Todo: use object field instead
                                    onClickMediaItem = { url -> onMediaItemClicked.invoke(url) },
                                    dateOfLastUpdate = generateRandomDate() //Todo: use object field instead
                                )
                            }
                        }
                    }

                    ButtonClose(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { onDismiss.invoke() }
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
            text = stringResource(id = R.string.media_dialog_label_close),
            fontSize = 12.sp,
            color = colorResource(R.color.textPrimary)
        )
    }
}

@Composable
private fun MediaData(
    title: String,
    url: String,
    duration: String,
    dateOfLastUpdate: String,
    onClickMediaItem: (url: String) -> Unit,
) {
    val textSize = remember { 12.sp }
    val labelDuration = stringResource(R.string.media_dialog_label_duration)
    val labelUpdate = stringResource(R.string.media_dialog_label_update)

    Column(Modifier.clickable { onClickMediaItem.invoke(url) }) {
        Text(
            text = title,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
        )
        Row {
            Text(
                text = "$labelDuration:$SPACE_STRING",
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = duration,
                fontSize = textSize,
            )
        }
        Row {
            Text(
                text = "$labelUpdate:$SPACE_STRING",
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = dateOfLastUpdate,
                fontSize = textSize,
            )
        }
    }
}

@Composable
private fun MediaDialogItemIcon(
    type: MediaType,
    size: Dp,
) {
    val mediaDrawableIcon = if (MediaType.VIDEO == type) {
        R.drawable.media_dialog_video
    } else {
        R.drawable.media_dialog_audio
    }
    Image(
        painter = painterResource(mediaDrawableIcon),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = Modifier.size(size)
    )
}

@Composable
private fun MediaDialogMainIcon() {
    val imageSize = 72.dp
    val imageDrawable = R.drawable.media_dialog_icon

    Image(
        painter = painterResource(imageDrawable),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = Modifier.size(imageSize)
    )
}

@Composable
private fun MediaDialogItem(
    type: MediaType,
    title: String,
    url: String,
    duration: String,
    dateOfLastUpdate: String,
    onClickMediaItem: (url: String) -> Unit,
) {
    Column(
        Modifier
            .background(Color.White)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = colorResource(R.color.colorPrimary_b0b),
                    shape = RoundedCornerShape(1)
                )
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp,
                    horizontal = 8.dp
                )
        ) {
            MediaDialogItemIcon(
                type = type,
                size = 56.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            MediaData(
                title = title,
                url = url,
                duration = duration,
                dateOfLastUpdate = dateOfLastUpdate,
                onClickMediaItem = onClickMediaItem
            )
        }
    }
}

enum class MediaType {
    VIDEO,
    AUDIO,
}

fun generateRandomDate(): String {
    val random = Random(System.currentTimeMillis())

    val day = random.nextInt(1, 32)
    val month = random.nextInt(9, 13)
    val year = 2023

    return "%02d-%02d-%04d".format(day, month, year)
}

fun generateRandomDuration(): String {
    val random = Random(System.currentTimeMillis())
    val hour = random.nextInt(0, 24)
    val minute = random.nextInt(0, 60)

    return "%02d:%02d minutos".format(hour, minute)
}

fun generateRandomURLs(): List<String> {
    val urls = listOf("url0", "url1", "url2")
    val random = Random(System.currentTimeMillis())
    val count = random.nextInt(
        from = 1,
        until = 3
    ) // Max of 3 media items for videos/audio respectively

    return List(count) {
        val randomIndex = random.nextInt(from = 0, until = urls.size)
        urls[randomIndex]
    }
}
