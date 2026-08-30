package com.chittagong.localnews.di

import com.chittagong.localnews.data.repository.AuthRepositoryImpl
import com.chittagong.localnews.data.repository.PostRepositoryImpl
import com.chittagong.localnews.data.repository.UserRepositoryImpl
import com.chittagong.localnews.domain.repository.AuthRepository
import com.chittagong.localnews.domain.repository.PostRepository
import com.chittagong.localnews.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds the domain interfaces to their Firebase-backed implementations. */
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
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}
