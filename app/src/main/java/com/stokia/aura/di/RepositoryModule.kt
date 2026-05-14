package com.stokia.aura.di

import com.stokia.aura.data.repository.AuthRepositoryImpl
import com.stokia.aura.data.repository.UserRepositoryImpl
import com.stokia.aura.domain.repository.AuthRepository
import com.stokia.aura.domain.repository.UserRepository
import com.stokia.aura.data.repository.ContactRepositoryImpl
import com.stokia.aura.domain.repository.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository
}
