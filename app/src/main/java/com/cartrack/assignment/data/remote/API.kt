package com.cartrack.assignment.data.remote

import com.cartrack.assignment.data.bean.User
import io.reactivex.Single
import retrofit2.http.GET

interface API {

    @GET("users")
    abstract fun getUsers(): Single<List<User>>
}