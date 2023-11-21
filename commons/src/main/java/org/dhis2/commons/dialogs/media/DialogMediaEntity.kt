package org.dhis2.commons.dialogs.media

import android.os.Parcel
import android.os.Parcelable

data class DialogMediaEntity(
    // Add uid
    val title: String,
    val duration: String,
    val dateOfLastUpdate: String,
    val url: String, // Todo: change to 'String?'
    val dialogMediaType: DialogMediaType
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(DialogMediaType::class.java.classLoader) ?: DialogMediaType.UNKNOWN
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(duration)
        parcel.writeString(dateOfLastUpdate)
        parcel.writeString(url)
        parcel.writeParcelable(dialogMediaType, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DialogMediaEntity> =
            object : Parcelable.Creator<DialogMediaEntity> {
                override fun createFromParcel(parcel: Parcel): DialogMediaEntity {
                    return DialogMediaEntity(parcel)
                }

                override fun newArray(size: Int): Array<DialogMediaEntity?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
