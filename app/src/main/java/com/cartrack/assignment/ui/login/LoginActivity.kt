package com.cartrack.assignment.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.cartrack.assignment.R
import com.cartrack.assignment.ui.base.BaseActivity
import com.cartrack.assignment.ui.main.MainActivity
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.CountryPicker.SORT_BY_NAME
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity(), OnCountryPickerListener {
    private val viewModel: LoginViewModel by viewModel()
    private var curWatchPassword: WatchPassword = WatchPassword.InVisible
    private lateinit var countryPicker: CountryPicker
    private var selectCountry: Country? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initViews()
        subscribeDataChange()
    }

    override fun onSelectCountry(country: Country) {
        displayLoginInfoError(false)
        editCountry.setText(country.name)
        viewModel.setCountryCode(country.code)
        selectCountry = country
    }

    override fun displayLoading(isLoading: Boolean) {
        cpbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        viewLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun initViews() {
        //first time set password format is disable
        imgAlphanumeric.isEnabled = false
        img8char.isEnabled = false

        setTouchEffect()
        setClickListener()
        setEditTextEvent()
    }

    private fun subscribeDataChange() {
        subscribePasswordFormat(viewModel.checkPasswordFormat)
        subscribeEnableLoginButton(viewModel.enableLogin)
        subscribeLoginEvent(viewModel.onLoginResult)
        subscribeLoadingEvent(viewModel.isLoading)
    }

    private fun setTouchEffect() {
        viewEyes.setOnTouchListener { _, motionEvent ->
            imgEyes.onTouchEvent(motionEvent)
            false
        }
        editCountry.setOnTouchListener { _, motionEvent ->
            imgArrow.onTouchEvent(motionEvent)
            false
        }
    }

    private fun setClickListener() {
        viewEyes.setOnClickListener {
            when (curWatchPassword) {
                WatchPassword.Visible -> {
                    imgEyes.setImageResource(R.drawable.ic_eye_open)
                    editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    editPassword.setSelection(editPassword.text.toString().length)
                    curWatchPassword = WatchPassword.InVisible
                }
                WatchPassword.InVisible -> {
                    imgEyes.setImageResource(R.drawable.ic_eye_close)
                    editPassword.transformationMethod = null
                    editPassword.setSelection(editPassword.text.toString().length)
                    curWatchPassword = WatchPassword.Visible
                }
            }
        }

        editCountry.setOnClickListener {
            showCountryPicker()
        }

        tvLogin.setOnClickListener {
            viewModel.login(editName.text.toString(), editPassword.text.toString(), selectCountry!!.code)
        }
    }

    private fun setEditTextEvent() {
        editName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.let {
                    displayLoginInfoError(false)
                    viewModel.setUserName(it.toString())
                }
            }
        })
        editPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.let {
                    displayLoginInfoError(false)
                    viewModel.setPassword(it.toString())
                }
            }
        })
    }

    private fun showCountryPicker() {
        countryPicker = CountryPicker.Builder()
            .with(this@LoginActivity)
            .listener(this@LoginActivity)
            .canSearch(false)
            .sortBy(SORT_BY_NAME)
            .build()
        countryPicker.showDialog(this@LoginActivity)
    }

    private fun showPasswordFormatResult(format: Format) {
        when (format) {
            Format.ErrorAlphanumeric -> {
                imgAlphanumeric.isEnabled = false
                tvAlphanumeric.isEnabled = false
                img8char.isEnabled = true
                tv8char.isEnabled = true
            }
            Format.ErrorAtLeast8 -> {
                imgAlphanumeric.isEnabled = true
                tvAlphanumeric.isEnabled = true
                img8char.isEnabled = false
                tv8char.isEnabled = false
            }
            Format.BothError -> {
                imgAlphanumeric.isEnabled = false
                tvAlphanumeric.isEnabled = false
                img8char.isEnabled = false
                tv8char.isEnabled = false
            }
            Format.Correct -> {
                imgAlphanumeric.isEnabled = true
                tvAlphanumeric.isEnabled = true
                img8char.isEnabled = true
                tv8char.isEnabled = true
            }
        }
    }

    private fun displayLoginInfoError(show: Boolean) {
        tvError.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun subscribePasswordFormat(state: Observable<Format>) {
        state
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                showPasswordFormatResult(it)
            }, {
                showError(it)
            })
            .disposeOnDestroy()
    }

    private fun subscribeEnableLoginButton(state: Observable<Boolean>) {
        state
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                tvLogin.isEnabled = it
            }, {
                showError(it)
            })
            .disposeOnDestroy()
    }

    private fun subscribeLoginEvent(state: Observable<Boolean>) {
        state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it) {
                    MainActivity.startActivity(this)
                } else {
                    displayLoginInfoError(true)
                }
            }, {
                showError(it)
            })
            .disposeOnDestroy()
    }

    private fun subscribeLoadingEvent(state: Observable<Boolean>) {
        state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                displayLoading(it)
            }, {
                showError(it)
            })
            .disposeOnDestroy()
    }

    sealed class WatchPassword {
        object Visible : WatchPassword()
        object InVisible : WatchPassword()
    }
}
