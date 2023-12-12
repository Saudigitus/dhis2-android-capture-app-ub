package org.dhis2.usescases.teiDashboard.dashboardfragments.relationships

import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.recyclerview.widget.RecyclerView
import org.dhis2.commons.data.RelationshipViewModel
import org.dhis2.commons.resources.setItemPic
import org.dhis2.databinding.ItemRelationshipBinding
import org.dhis2.ui.MetadataIconData
import org.dhis2.ui.setUpMetadataIcon
import timber.log.Timber

class RelationshipViewHolder(
    private val binding: ItemRelationshipBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.composeToImage.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
    }

    fun bind(presenter: RelationshipPresenter, relationships: RelationshipViewModel) {
        binding.apply {
            relationshipCard.setOnClickListener {
                if (relationships.canBeOpened) {
                    presenter.onRelationshipClicked(
                        relationships.ownerType,
                        relationships.ownerUid
                    )
                }
            }
            clearButton.apply {
                visibility = if (relationships.canBeOpened) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                setOnClickListener {
                    relationships.relationship.uid()?.let { presenter.deleteRelationship(it) }
                }
            }
//            toRelationshipName.text = relationships.displayRelationshipName()
//            relationshipGender.text = relationships.displayRelationshipTypeName()
//            relationshipAge.text = relationships.toValues[0].second

            toRelationshipName.text = relationships.toValues.getOrNull(0)?.second

            textGender.text = relationships.toValues.getOrNull(1)?.first
            relationshipGender.text = relationships.toValues.getOrNull(1)?.second

            textAge.text = relationships.fromValues.getOrNull(2)!!.first
            relationshipAge.text = relationships.fromValues.getOrNull(2)?.second

            //relationships.
            Timber.tag("REL_OBJ").d("$relationships")
            relationships.displayImage().let { (imagePath, defaultRes) ->
                if (relationships.isEvent()) {
                    binding.composeToImage.setUpMetadataIcon(
                        MetadataIconData(
                            programColor = relationships.ownerDefaultColorResource,
                            iconResource = defaultRes,
                            sizeInDp = 40
                        ),
                        false
                    )
                } else {
                    toTeiImage.setItemPic(
                        imagePath,
                        defaultRes,
                        relationships.ownerDefaultColorResource,
                        relationships.displayRelationshipName(),
                        relationships.isEvent(),
                        binding.imageText
                    )
                }
            }
        }
    }
}
