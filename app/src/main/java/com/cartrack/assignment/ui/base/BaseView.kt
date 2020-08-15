package com.cartrack.assignment.ui.base

interface BaseView {

    fun displayLoading(isLoading : Boolean)

    fun showError(throwable: Throwable)

}