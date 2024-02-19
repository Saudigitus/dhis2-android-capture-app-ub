package org.dhis2.usescases.main.program

import android.os.Parcel
import android.os.Parcelable
import org.dhis2.ui.MetadataIconData
import org.hisp.dhis.android.core.common.State

data class ProgramViewModel(
    val uid: String,
    val title: String,
    val metadataIconData: MetadataIconData,
    val count: Int,
    val type: String?,
    val typeName: String,
    val programType: String,
    val description: String?,
    val onlyEnrollOnce: Boolean,
    val accessDataWrite: Boolean,
    val state: State,
    val hasOverdueEvent: Boolean,
    val filtersAreActive: Boolean,
    val downloadState: ProgramDownloadState,
    val downloadActive: Boolean = false,
    val reference: String? = null
): Parcelable {




    private var hasShownCompleteSyncAnimation = false

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("metadataIconData"),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        TODO("state"),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        TODO("downloadState"),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )

//    {
//        hasShownCompleteSyncAnimation = parcel.readByte() != 0.toByte()
//    }

    fun setCompleteSyncAnimation() {
        hasShownCompleteSyncAnimation = true
    }

    fun hasShowCompleteSyncAnimation() = hasShownCompleteSyncAnimation

    fun translucent(): Boolean {
        return (filtersAreActive && count == 0) || downloadState == ProgramDownloadState.DOWNLOADING
    }

    fun countDescription() = "%s %s".format(count, typeName)

    fun isDownloading() = downloadActive || downloadState == ProgramDownloadState.DOWNLOADING

    fun getAlphaValue() = if (isDownloading()) {
        0.5f
    } else {
        1f
    }


    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<ProgramViewModel> {
        override fun createFromParcel(parcel: Parcel): ProgramViewModel {
            return ProgramViewModel(parcel)
        }

        override fun newArray(size: Int): Array<ProgramViewModel?> {
            return arrayOfNulls(size)
        }
    }
}

enum class ProgramDownloadState {
    DOWNLOADING, DOWNLOADED, ERROR, NONE
}
