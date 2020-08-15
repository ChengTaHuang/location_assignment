package com.cartrack.assignment.di

import androidx.room.Room
import com.cartrack.assignment.data.local.DB
import com.cartrack.assignment.data.local.DatabaseCallback
import com.cartrack.assignment.ui.login.LoginModel
import com.cartrack.assignment.ui.login.LoginModelImpl
import com.cartrack.assignment.ui.login.LoginViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            DB::class.java,
            DB::class.java.simpleName
        )
            .addCallback(DatabaseCallback())
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<DB>().accountDao() }
    factory<LoginModel> {
        LoginModelImpl(get())
    }
    viewModel { LoginViewModel(get()) }
}