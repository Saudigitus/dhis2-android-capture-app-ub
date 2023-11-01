package org.dhis2.commons.dialogs.media

import android.os.Parcel
import android.os.Parcelable

enum class DialogMediaType : Parcelable {
    VIDEO,
    AUDIO,
    UNKNOWN;

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DialogMediaType> =
            object : Parcelable.Creator<DialogMediaType> {
                override fun createFromParcel(parcel: Parcel): DialogMediaType {
                    return values()[parcel.readInt()]
                }

                override fun newArray(size: Int): Array<DialogMediaType?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
