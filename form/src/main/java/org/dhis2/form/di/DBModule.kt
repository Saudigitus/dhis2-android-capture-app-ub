package org.dhis2.form.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.dhis2.form.data.MediaRepositoryImpl
import org.dhis2.form.local.AppDatabase
import org.dhis2.form.ui.MediaRepository
import org.hisp.dhis.android.core.D2
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    /**
     * Inject MediaRepository
     */
    @Provides
    @Singleton fun providesMediaRepository(appDatabase: AppDatabase): MediaRepository {
        return MediaRepositoryImpl(appDatabase.mediaDAO())
    }

    /***
     * Inject AppDatabase
     */
    @Provides
    @Singleton fun providesAppDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        ).build()


}