package org.dhis2.form.local

import androidx.room.Database
import androidx.room.*
import org.dhis2.form.data.media.MediaDetails

@Database(
    entities = [MediaDetails::class],
    version  = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun mediaDAO(): MediaDetailsDAO

    companion object {
        const val DB_NAME = "media_db"
    }
}