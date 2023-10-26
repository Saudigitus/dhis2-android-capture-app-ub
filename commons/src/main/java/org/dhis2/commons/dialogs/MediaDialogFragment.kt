package org.dhis2.commons.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment

class MediaDialogFragment : DialogFragment() {

    private var title: String? = null
    private var subTitle: String? = null
    private var mediaEntities: List<DialogMediaEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            title = it.getString("title")
            subTitle = it.getString("subTitle")
            mediaEntities = it.getParcelableArrayList("mediaEntities")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaDialog(
                    title = title ?: randomTitle(),
                    subTitle = subTitle ?: randomSubTitle(),
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

        fun newInstance(
            title: String?,
            subTitle: String?,
            mediaEntities: List<DialogMediaEntity>?,
        ): MediaDialogFragment {
            val fragment = MediaDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("subTitle", subTitle)
            args.putParcelableArrayList("mediaEntities", ArrayList(mediaEntities))
            fragment.arguments = args
            return fragment
        }
    }
}
