package com.cartrack.assignment.ui.main

import com.cartrack.assignment.BaseTest
import com.cartrack.assignment.ui.TestUsers
import com.cartrack.assignment.ui.login.Format
import com.cartrack.assignment.ui.login.LoginModel
import com.cartrack.assignment.ui.login.LoginViewModel
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.TimeUnit

class MainViewModelTest : BaseTest() {
    private val model = Mockito.mock(MainModel::class.java)
    private lateinit var viewModel: MainViewModel

    @Test
    fun `observe users`() {
        viewModel = MainViewModel(model)
        val users = TestUsers.getUsers()

        Mockito.`when`(model.isNetworkConnected()).thenReturn(Single.just(true))
        Mockito.`when`(model.getUsers()).thenReturn(Single.just(users))

        val test = viewModel.users.test()

        viewModel.getUsers()
        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValue(users)
    }

    @Test
    fun `observe users api loading status`(){
        viewModel = MainViewModel(model)
        val users = TestUsers.getUsers()

        Mockito.`when`(model.isNetworkConnected()).thenReturn(Single.just(true))
        Mockito.`when`(model.getUsers()).thenReturn(Single.just(users))

        val test = viewModel.isLoading.test()

        viewModel.getUsers()

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValueAt(0,true)
        test.assertValueAt(1,false)
    }

    @Test
    fun `observe network connected`(){
        viewModel = MainViewModel(model)
        val users = TestUsers.getUsers()

        Mockito.`when`(model.isNetworkConnected()).thenReturn(Single.just(true))
        Mockito.`when`(model.getUsers()).thenReturn(Single.just(users))

        val test = viewModel.isConnectedNetwork.test()

        viewModel.getUsers()

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValue(true)
    }

    @Test
    fun `observe network not connect`(){
        viewModel = MainViewModel(model)
        val throwable = MainViewModel.NetworkConnectException()
        Mockito.`when`(model.isNetworkConnected()).thenReturn(Single.error(throwable))

        val test = viewModel.isConnectedNetwork.test()

        viewModel.getUsers()

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValue(false)
    }

    @Test
    fun `observe api error`(){
        viewModel = MainViewModel(model)
        val throwable = Throwable()

        Mockito.`when`(model.isNetworkConnected()).thenReturn(Single.just(true))
        Mockito.`when`(model.getUsers()).thenReturn(Single.error(throwable))

        val test = viewModel.apiError.test()

        viewModel.getUsers()

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValue(throwable)
    }
}