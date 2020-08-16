package com.cartrack.assignment.ui.main

import com.cartrack.assignment.data.bean.User
import com.cartrack.assignment.data.remote.NetworkService
import io.reactivex.Single

interface MainModel {

    fun isNetworkConnected(): Single<Boolean>

    fun getUsers(): Single<List<User>>
}

class MainModelImpl(private val service: NetworkService) : MainModel {

    override fun isNetworkConnected(): Single<Boolean> = Single.just(service.isConnected())

    override fun getUsers(): Single<List<User>> = service.api.getUsers()
}