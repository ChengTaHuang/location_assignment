package com.cartrack.assignment.ui.login

import androidx.room.EmptyResultSetException
import com.cartrack.assignment.BaseTest
import com.cartrack.assignment.data.bean.Account
import com.cartrack.assignment.data.local.AccountDao
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito

class LoginModelTest : BaseTest() {
    private val accountDao = Mockito.mock(AccountDao::class.java)

    private lateinit var model: LoginModel
    private val userName = "test"
    private val password = "test1234"
    private val countryCode = "SG"

    @Test
    fun `Incorrect password,enough characters , but no alphanumeric`() {
        model = LoginModelImpl(accountDao)

        val noAlphanumericPassword = "12345678"
        val test = model.checkPasswordFormat().test()

        model.setPassword(noAlphanumericPassword)

        test.assertValue(Format.ErrorAlphanumeric)
    }

    @Test
    fun `Incorrect password,has alphanumeric, but less than 8 characters`() {
        model = LoginModelImpl(accountDao)

        val lessThan8CharPassword = "abc123"
        val test = model.checkPasswordFormat().test()

        model.setPassword(lessThan8CharPassword)

        test.assertValue(Format.ErrorAtLeast8)
    }

    @Test
    fun `Incorrect password,has not alphanumeric and less than 8 characters`() {
        model = LoginModelImpl(accountDao)

        val noAlphanumericAndLessThan8CharsPassword = "a"
        val test = model.checkPasswordFormat().test()

        model.setPassword(noAlphanumericAndLessThan8CharsPassword)

        test.assertValue(Format.BothError)
    }

    @Test
    fun `correct password`() {
        model = LoginModelImpl(accountDao)

        val password = "test1234"
        val test = model.checkPasswordFormat().test()

        model.setPassword(password)

        test.assertValue(Format.Correct)
    }

    @Test
    fun `enable Login button , username , password and country is valid`() {
        model = LoginModelImpl(accountDao)

        val test = model.enableLogin().test()

        model.setUserName(userName)
        model.setPassword(password)
        model.setCountryCode(countryCode)
        test.assertValue(true)
    }

    @Test
    fun `login success`() {
        model = LoginModelImpl(accountDao)
        val databaseAccount = Account(1, userName, password, countryCode)
        Mockito.`when`(accountDao.getAccount(userName, password, countryCode))
            .thenReturn(Single.just(databaseAccount))

        val test = model.login(userName, password, countryCode).test()
        test.assertValue(true)
    }

    @Test
    fun `login fail`() {
        model = LoginModelImpl(accountDao)
        val throwable = EmptyResultSetException("EmptyResultSetException")
        Mockito.`when`(accountDao.getAccount(userName, password, countryCode))
            .thenReturn(Single.error(throwable))

        val test = model.login(userName, password, countryCode).test()
        test.assertError(throwable)
    }
}