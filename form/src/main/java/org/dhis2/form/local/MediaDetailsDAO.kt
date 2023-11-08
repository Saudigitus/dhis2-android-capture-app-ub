package org.dhis2.form.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.dhis2.form.data.media.MediaDetails
import java.util.concurrent.Flow

@Dao
interface MediaDetailsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(mediaDetails: MediaDetails)

    @Query("SELECT * FROM MediaDetails ORDER BY id DESC")
    fun getAll(): List<MediaDetails>

    @Query("SELECT * FROM MediaDetails LIMIT :size")
    fun getAllByLimit(size: Int): List<MediaDetails>

    @Query("SELECT * FROM MediaDetails WHERE id LIKE :id")
    fun getDetailsById(id: String): MediaDetails

    @Delete
    suspend fun delete(mediaDetails: MediaDetails)
}