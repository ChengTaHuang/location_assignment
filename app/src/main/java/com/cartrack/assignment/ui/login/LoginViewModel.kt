package com.cartrack.assignment.ui.login

import androidx.room.EmptyResultSetException
import com.cartrack.assignment.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject

class LoginViewModel(private val model: LoginModel) : BaseViewModel() {
    private val onSuccessSource = PublishSubject.create<Boolean>()
    private val isLoadingSource = PublishSubject.create<Boolean>()
    val checkPasswordFormat: Observable<Format>
        get() = model.checkPasswordFormat().distinctUntilChanged()

    val enableLogin: Observable<Boolean>
        get() = model.enableLogin()

    val onLoginResult : Observable<Boolean>
        get() = onSuccessSource

    val isLoading : Observable<Boolean>
        get() = isLoadingSource

    fun setUserName(name: String) {
        model.setUserName(name)
    }

    fun setPassword(password: String) {
        model.setPassword(password)
    }

    fun setCountryCode(code: String) {
        model.setCountryCode(code)
    }

    fun login(name : String , password: String , code : String){
        model.login(name,password,code)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingSource.onNext(true) }
            .doFinally { isLoadingSource.onNext(false) }
            .subscribe({
                onSuccessSource.onNext(it)
            }, {
                if(it is EmptyResultSetException){
                    onSuccessSource.onNext(false)
                }else {
                    onSuccessSource.onError(it)
                }
            })
            .disposeOnDestroy()
    }
}