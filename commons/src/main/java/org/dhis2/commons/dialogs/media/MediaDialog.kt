package org.dhis2.commons.dialogs.media

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import kotlin.random.Random
import org.dhis2.commons.R
import org.dhis2.commons.R.color
import org.dhis2.commons.dialogs.util.Constants.SPACE_STRING
import org.dhis2.commons.extensions.playMedia

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PreviewMediaDialog() {
    MediaDialog(
        title = randomTitle(),
        subTitle = randomSubTitle(),
        mediaEntities = randomMediaEntities(),
        onDismiss = {}
    )
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewVideoMediaItem() {
    MediaDialogItem(
        dialogMediaType = DialogMediaType.VIDEO,
        title = randomTitle(),
        url = randomAudioURLs().random(),
        duration = randomDuration(),
        dateOfLastUpdate = randomDate()
    ) {}
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewAudioMediaItem() {
    MediaDialogItem(
        dialogMediaType = DialogMediaType.AUDIO,
        title = randomTitle(),
        url = randomAudioURLs().random(),
        duration = randomDuration(),
        dateOfLastUpdate = randomDate()
    ) {}
}

@Composable
fun MediaDialog(
    title: String,
    subTitle: String,
    mediaEntities: List<DialogMediaEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sortedMediaEntities = sortMediaEntities(mediaEntities)

    Dialog(
        onDismissRequest = { onDismiss.invoke() }
    ) {
        Surface(
            elevation = 12.dp,
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
                        MediaDialogIcon(
                            imageDrawable = R.drawable.media_dialog_hands,
                            imageSize = 72.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = subTitle,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = colorResource(id = color.colorPrimary_f57)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(sortedMediaEntities) { mediaEntity ->
                            when (mediaEntity.dialogMediaType) {
                                DialogMediaType.VIDEO,
                                DialogMediaType.AUDIO,
                                DialogMediaType.UNKNOWN
                                -> {
                                    MediaDialogItem(
                                        dialogMediaType = mediaEntity.dialogMediaType,
                                        title = mediaEntity.title,
                                        url = mediaEntity.url,
                                        duration = mediaEntity.duration,
                                        onClickMediaItem = { url ->
                                            context.playMedia(mediaUrl = url)
                                        },
                                        dateOfLastUpdate = mediaEntity.dateOfLastUpdate
                                    )
                                }
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
private fun ButtonClose(modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(0.5.dp, colorResource(color.colorPrimary_b0b)),
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.media_dialog_label_close),
            fontSize = 12.sp,
            color = colorResource(color.textPrimary)
        )
    }
}

@Composable
private fun MediaData(
    title: String,
    url: String,
    duration: String,
    dateOfLastUpdate: String,
    onClickMediaItem: (url: String) -> Unit
) {
    Column(Modifier.clickable { onClickMediaItem.invoke(url) }) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Row {
            Text(
                text = "${stringResource(R.string.media_dialog_label_duration)}:$SPACE_STRING",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = duration,
                fontSize = 12.sp
            )
        }
        Row {
            Text(
                text = "${stringResource(R.string.media_dialog_label_update)}:$SPACE_STRING",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateOfLastUpdate,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MediaDialogItem(
    dialogMediaType: DialogMediaType,
    title: String,
    url: String,
    duration: String,
    dateOfLastUpdate: String,
    onClickMediaItem: (url: String) -> Unit
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
                    color = colorResource(color.colorPrimary_b0b),
                    shape = RoundedCornerShape(1)
                )
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp,
                    horizontal = 8.dp
                )
        ) {
            MediaDialogIcon(
                imageDrawable = getMediaIconByType(dialogMediaType = dialogMediaType),
                imageSize = 56.dp
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

private fun getMediaIconByType(dialogMediaType: DialogMediaType): Int {
    return if (DialogMediaType.VIDEO == dialogMediaType) {
        R.drawable.media_dialog_video
    } else {
        R.drawable.media_dialog_audio
    }
}

private fun sortMediaEntities(mediaEntities: List<DialogMediaEntity>): List<DialogMediaEntity> {
    return mediaEntities.sortedByDescending {
        if (it.dialogMediaType == DialogMediaType.VIDEO) 1 else 0
    }
}

private fun randomMediaType(): DialogMediaType {
    val values = DialogMediaType.values()
    val randomIndex = Random.nextInt(values.size)
    return values[randomIndex]
}

@Composable
private fun MediaDialogIcon(imageDrawable: Int, imageSize: Dp) {
    Image(
        painter = painterResource(imageDrawable),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = Modifier.size(imageSize)
    )
}

private fun randomMediaEntity(): DialogMediaEntity {
    val randomMediaType = randomMediaType()
    val randomUrl: String = if (randomMediaType == DialogMediaType.VIDEO) {
        randomVideoURLs().random()
    } else {
        randomAudioURLs().random()
    }

    return DialogMediaEntity(
        title = randomTitle(),
        duration = randomDuration(),
        dateOfLastUpdate = randomDate(),
        url = randomUrl,
        dialogMediaType = randomMediaType
    )
}

fun randomTitle(): String = listOf(
    "Como lavar as mãos",
    "Como escolher o sabonete certo",
    "A maneira errada de lavar as mãos",
    "Devo usar cinzas de carvão?"
).random()

fun randomSubTitle(): String = listOf(
    "Pode ser uma torneira com água canalizada ou recipientes",
    "Em diferentes cenários, pode ser uma torneira com água canalizada.",
    "É válido ter um recipiente e água canalizada"
).random()

private fun randomDate(): String {
    val random = Random(System.currentTimeMillis())

    val day = random.nextInt(1, 32)
    val month = random.nextInt(9, 13)
    val year = 2023

    return "%02d-%02d-%04d".format(day, month, year)
}

private fun randomDuration(): String {
    val random = Random(System.currentTimeMillis())
    val hour = random.nextInt(0, 24)
    val minute = random.nextInt(0, 60)

    return "%02d:%02d minutos".format(hour, minute)
}

fun randomMediaEntities(): List<DialogMediaEntity> {
    val maxListSize = 3
    val listSize = Random.nextInt(1, maxListSize)
    val mediaEntities = mutableListOf<DialogMediaEntity>()

    repeat(listSize) {
        mediaEntities.add(element = randomMediaEntity())
    }
    return mediaEntities
}

fun randomVideoURLs(): List<String> = listOf(
    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
)

private fun randomAudioURLs(): List<String> = listOf(
    "https://actions.google.com/sounds/v1/weather/rain_heavy_loud.ogg",
    "https://actions.google.com/sounds/v1/emergency/ambulance_siren_distant.ogg"
)
