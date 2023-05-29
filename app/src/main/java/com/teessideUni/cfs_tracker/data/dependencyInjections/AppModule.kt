package com.teessideUni.cfs_tracker.data.dependencyInjections

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.data.repository.AuthRepositoryImpl
import com.teessideUni.cfs_tracker.data.repository.HeartRateRepositoryImpl
import com.teessideUni.cfs_tracker.data.repository.RespiratoryRateRepositoryImpl
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import com.teessideUni.cfs_tracker.domain.repository.HeartRateRepository
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore) : AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun providesHeartRateRepositoryImpl(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore) : HeartRateRepository {
        return HeartRateRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun providesRespiratoryRateRepositoryImpl(context: Context , firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore) : RespiratoryRateRepository {
        return RespiratoryRateRepositoryImpl(context, firebaseAuth, firestore)
    }
}