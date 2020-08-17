package com.cartrack.assignment.ui.main

import com.cartrack.assignment.BaseTest
import com.cartrack.assignment.data.remote.API
import com.cartrack.assignment.data.remote.NetworkService
import com.cartrack.assignment.ui.TestUsers
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito

class MainModelTest : BaseTest() {
    private val api = Mockito.mock(API::class.java)
    private val service = Mockito.mock(NetworkService::class.java)
    private lateinit var model: MainModel

    @Test
    fun `get network connected`() {
        model = MainModelImpl(service)

        Mockito.`when`(service.isConnected()).thenReturn(true)

        val test = model.isNetworkConnected().test()

        test.assertValue(true)

        Mockito.`when`(service.isConnected()).thenReturn(false)

        val test2 = model.isNetworkConnected().test()

        test2.assertValue(false)
    }

    @Test
    fun `get users`(){
        model = MainModelImpl(service)

        val users = TestUsers.getUsers()
        Mockito.`when`(service.api).thenReturn(api)
        Mockito.`when`(service.api.getUsers()).thenReturn(Single.just(users))

        val test = model.getUsers().test()
        test.assertValue(users)
    }
}