package com.cartrack.assignment.ui.base

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity(), BaseView {
    private val disposables = CompositeDisposable()
    private lateinit var keyboardManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keyboardManager = baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun displayLoading(isLoading: Boolean) {

    }

    override fun showError(throwable: Throwable) {

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        checkToHideKeyBoardIfOutsideOfEditText(event)
        return super.dispatchTouchEvent(event)
    }

    protected fun Disposable.disposeOnDestroy(): Disposable {
        disposables.add(this)
        return disposables
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        keyboardManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(view: View) {
        view.clearFocus()
        keyboardManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkToHideKeyBoardIfOutsideOfEditText(event: MotionEvent){
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    Log.d("focus", "touchevent")
                    hideKeyboard(v)
                }
            }
        }
    }
}