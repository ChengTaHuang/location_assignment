package com.cartrack.assignment.ui.main

import com.cartrack.assignment.data.bean.User
import com.cartrack.assignment.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class MainViewModel(private val mainModel: MainModel) : BaseViewModel() {
    private val usersSource = PublishSubject.create<List<User>>()
    private val isLoadingSource = PublishSubject.create<Boolean>()
    private val isConnected = PublishSubject.create<Boolean>()
    private val apiErrorSource = PublishSubject.create<Throwable>()

    val users: Observable<List<User>>
        get() = usersSource

    val isLoading: Observable<Boolean>
        get() = isLoadingSource

    val isConnectedNetwork: Observable<Boolean>
        get() = isConnected

    val apiError: Observable<Throwable>
        get() = apiErrorSource

    fun getUsers() {
        mainModel.isNetworkConnected()
            .flatMap { isConnected ->
                if (isConnected) mainModel.getUsers()
                else throw NetworkConnectException()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoadingSource.onNext(true) }
            .doFinally { isLoadingSource.onNext(false) }
            .subscribe({
                isConnected.onNext(true)
                usersSource.onNext(it)
            }, {
                if (it is NetworkConnectException ||
                    it is SocketTimeoutException ||
                    it is HttpException
                ) {
                    isConnected.onNext(false)
                } else {
                    apiErrorSource.onNext(it)
                }
            })
            .disposeOnDestroy()

    }

    class NetworkConnectException : Exception()
}