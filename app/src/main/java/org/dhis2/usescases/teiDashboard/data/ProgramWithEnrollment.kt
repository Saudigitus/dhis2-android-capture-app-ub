package org.dhis2.usescases.teiDashboard.data

import android.os.Parcel
import android.os.Parcelable
import org.dhis2.ui.MetadataIconData

data class ProgramWithEnrollment(
    val programId: String?,
    val displayName: String?,
    val programType: String?,
    val typeName: String?,
    val enrollmentStatus: Boolean,
    val countDescription: Int = 0,
    val countEnrollment: Int? = null,
    val metadataIconData: MetadataIconData,
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("metadataIconData")
    ) {
    }


    companion object CREATOR : Parcelable.Creator<ProgramWithEnrollment> {
        override fun createFromParcel(parcel: Parcel): ProgramWithEnrollment {
            return ProgramWithEnrollment(parcel)
        }

        override fun newArray(size: Int): Array<ProgramWithEnrollment?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}
