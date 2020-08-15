package com.cartrack.assignment.ui.login

import android.util.Log
import com.cartrack.assignment.data.local.AccountDao
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface LoginModel {

    fun setUserName(name: String)

    fun setPassword(password: String)

    fun setCountryCode(code: String)

    fun checkPasswordFormat(): Observable<Format>

    fun enableLogin(): Observable<Boolean>

    fun login(name: String, password: String, countryCode: String): Single<Boolean>
}

class LoginModelImpl(private val accountDbSource : AccountDao) : LoginModel {
    private val userNameSource = PublishSubject.create<UserNameData>()
    private val passwordSource = PublishSubject.create<PasswordData>()
    private val checkPasswordSource = PublishSubject.create<Format>()
    private val countryCodeSource = PublishSubject.create<CountryData>()
    override fun setUserName(name: String) {
        userNameSource.onNext(UserNameData(name, name.isNotEmpty()))
    }

    override fun setPassword(password: String) {
        checkPasswordSource.onNext(isPasswordValid(password))
        passwordSource.onNext(
            PasswordData(
                password,
                isPasswordValid(password) == Format.Correct
            )
        )
    }

    override fun setCountryCode(code: String) =
        countryCodeSource.onNext(CountryData(code, code.isNotEmpty()))

    override fun checkPasswordFormat(): Observable<Format> = checkPasswordSource

    override fun enableLogin(): Observable<Boolean> =
        Observable.combineLatest(
            userNameSource,
            passwordSource,
            countryCodeSource,
            Function3<UserNameData, PasswordData, CountryData, Boolean> { userName, password, countryCode ->
                userName.valid && password.valid && countryCode.valid
            }
        )

    override fun login(name: String, password: String, countryCode: String): Single<Boolean> =
        accountDbSource.getAccount(name,password,countryCode).map { true }


    private fun isPasswordValid(password: String): Format {
        val isAlphanumeric = password.matches(".*[A-Za-z].*".toRegex())
                && password.matches(".*[0-9].*".toRegex())
                && password.matches("[A-Za-z0-9]*".toRegex())
        val over8char = password.length >= 8
        return if (isAlphanumeric && over8char) {
            Format.Correct
        } else if (isAlphanumeric && !over8char) {
            Format.ErrorAtLeast8
        } else if (!isAlphanumeric && over8char) {
            Format.ErrorAlphanumeric
        } else {
            Format.BothError
        }
    }

}

data class UserNameData(val userName: String, val valid: Boolean)
data class PasswordData(val password: String, val valid: Boolean)
data class CountryData(val countryCode: String, val valid: Boolean)

sealed class Format {
    object ErrorAlphanumeric : Format()
    object ErrorAtLeast8 : Format()
    object BothError : Format()
    object Correct : Format()
}