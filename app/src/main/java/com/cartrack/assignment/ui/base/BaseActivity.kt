package com.cartrack.assignment.ui.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), BaseView {

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError() {

    }
}