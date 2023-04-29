package com.teessideUni.cfs_tracker.dependencyInjections

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import com.teessideUni.cfs_tracker.data.repository.AuthRepositoryImpl
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


}