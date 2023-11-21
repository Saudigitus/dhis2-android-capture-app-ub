package org.dhis2.commons.dialogs.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment

class MediaDialogFragment : DialogFragment() {

    private var title: String? = null
    private var message: String? = null
    private var mediaEntities: List<DialogMediaEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            title = it.getString(MEDIA_DIALOG_TITLE)
            message = it.getString(MEDIA_DIALOG_MESSAGE)
            mediaEntities = it.getParcelableArrayList(MEDIA_DIALOG_MEDIA_ENTITIES)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaDialog(
                    title = title ?: randomTitle(),
                    subTitle = message ?: randomSubTitle(),
                    mediaEntities = mediaEntities ?: randomMediaEntities(),
                    onDismiss = {
                        dismiss()
                    }
                )
            }
        }
    }

    companion object {
        const val MEDIA_DIALOG_TAG = "media_dialog_tag"
        private const val MEDIA_DIALOG_TITLE = "title"
        private const val MEDIA_DIALOG_MESSAGE = "message"
        private const val MEDIA_DIALOG_MEDIA_ENTITIES = "media_entities"

        fun mediaDialog(
            title: String,
            message: String,
            mediaEntities: List<DialogMediaEntity>
        ): MediaDialogFragment {
            val mediaDialogFragment = MediaDialogFragment()
            val arguments = Bundle()
            arguments.putString(MEDIA_DIALOG_TITLE, title)
            arguments.putString(MEDIA_DIALOG_MESSAGE, message)
            arguments.putParcelableArrayList(MEDIA_DIALOG_MEDIA_ENTITIES, ArrayList(mediaEntities))
            mediaDialogFragment.arguments = arguments
            return mediaDialogFragment
        }
    }
}
