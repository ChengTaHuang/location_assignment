package com.cartrack.assignment.di

import com.cartrack.assignment.ui.login.LoginModel
import com.cartrack.assignment.ui.login.LoginModelImpl
import com.cartrack.assignment.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory<LoginModel> {
        LoginModelImpl()
    }
    viewModel { LoginViewModel(get()) }
}