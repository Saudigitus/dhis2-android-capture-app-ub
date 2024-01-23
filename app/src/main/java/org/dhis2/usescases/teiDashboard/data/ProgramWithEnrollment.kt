package org.dhis2.usescases.teiDashboard.data

import android.os.Parcel
import android.os.Parcelable

data class ProgramWithEnrollment(
    val programId: String?,
    val displayName: String?,
    val programType: String?,
    val typeName: String?,
    val enrollmentStatus: Boolean,
    val countDescription: Int = 0,
    val countEnrollment: Int? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(programId)
        parcel.writeString(displayName)
        parcel.writeString(programType)
        parcel.writeString(typeName)
        parcel.writeByte(if (enrollmentStatus) 1 else 0)
        parcel.writeInt(countDescription)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProgramWithEnrollment> {
        override fun createFromParcel(parcel: Parcel): ProgramWithEnrollment {
            return ProgramWithEnrollment(parcel)
        }

        override fun newArray(size: Int): Array<ProgramWithEnrollment?> {
            return arrayOfNulls(size)
        }
    }

}
