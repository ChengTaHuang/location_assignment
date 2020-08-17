package com.cartrack.assignment.ui.login

import androidx.room.EmptyResultSetException
import com.cartrack.assignment.BaseTest
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.TimeUnit

class LoginViewModelTest : BaseTest() {
    private val model = Mockito.mock(LoginModel::class.java)
    private lateinit var viewModel: LoginViewModel

    private val userName = "test"
    private val password = "test1234"
    private val countryCode = "SG"

    @Test
    fun `observe password format`() {
        viewModel = LoginViewModel(model)

        Mockito.`when`(model.checkPasswordFormat()).thenReturn(Observable.just(Format.Correct))

        val test = viewModel.checkPasswordFormat.test()
        test.assertValue(Format.Correct)
    }

    @Test
    fun `observe enable login`() {
        viewModel = LoginViewModel(model)

        Mockito.`when`(model.enableLogin()).thenReturn(Observable.just(true))

        val test = viewModel.enableLogin.test()
        test.assertValue(true)
    }

    @Test
    fun `observe login success result`(){
        viewModel = LoginViewModel(model)
        Mockito.`when`(model.login(userName,password,countryCode)).thenReturn(Single.just(true))

        val test = viewModel.onLoginResult.test()

        viewModel.login(userName,password,countryCode)
        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValue(true)
    }

    @Test
    fun `observe login fail result`(){
        viewModel = LoginViewModel(model)
        Mockito.`when`(model.login(userName,password,countryCode)).thenReturn(Single.just(false))

        val test = viewModel.onLoginResult.test()

        viewModel.login(userName,password,countryCode)
        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValue(false)
    }

    @Test
    fun `observe login fail result , because of EmptyResultSetException`(){
        val throwable = EmptyResultSetException("")
        viewModel = LoginViewModel(model)
        Mockito.`when`(model.login(userName,password,countryCode)).thenReturn(Single.error(throwable))

        val test = viewModel.onLoginResult.test()

        viewModel.login(userName,password,countryCode)
        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValue(false)
    }

    @Test
    fun `observe login and get throwable`(){
        val throwable = Throwable("")
        viewModel = LoginViewModel(model)
        Mockito.`when`(model.login(userName,password,countryCode)).thenReturn(Single.error(throwable))

        val test = viewModel.onLoginResult.test()

        viewModel.login(userName,password,countryCode)
        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertError(throwable)
    }

    @Test
    fun `observe login loading status`(){
        viewModel = LoginViewModel(model)
        Mockito.`when`(model.login(userName,password,countryCode)).thenReturn(Single.just(true))

        val test = viewModel.isLoading.test()

        viewModel.login(userName,password,countryCode)

        test.assertValueAt(0, true)

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValueAt(1,false)
    }
}