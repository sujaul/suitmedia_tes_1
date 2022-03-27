package com.dk.dksmtd.di

import com.galee.core.coreModule
import com.test.test_karim.Repository.UserDetailRepositoryImpl
import com.test.test_karim.Repository.guestsRepository
import com.test.test_karim.data.local.AppDatabase
import com.test.test_karim.data.mapper.UserMapper
import com.test.test_karim.data.remote.ApiService
import com.test.test_karim.feature.main.fourth.FourthVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val databaseModule = module {
    single { AppDatabase.getInstance(get()) }
}

val vmModule = module {
    viewModel { FourthVM(get()) }
}

val mapperModule = module {
    single { UserMapper() }
}

val serviceModule = module {
    factory <ApiService> { get<Retrofit>().create(ApiService::class.java) }
}

val repositoriesModule = module {
    single <guestsRepository>{ UserDetailRepositoryImpl(get()) }
}

val mainModule = listOf(
    coreModule,
    databaseModule,
    vmModule,
    mapperModule,
    serviceModule,
    repositoriesModule
)