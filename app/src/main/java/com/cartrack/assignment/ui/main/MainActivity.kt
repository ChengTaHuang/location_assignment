package com.cartrack.assignment.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cartrack.assignment.R
import com.cartrack.assignment.data.bean.User
import com.cartrack.assignment.ui.base.BaseActivity
import com.cartrack.assignment.ui.main.adapter.UserAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.cpbLoading
import kotlinx.android.synthetic.main.activity_login.viewLoading
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sub_bottom_sheet.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), OnMapReadyCallback {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var mMap: GoogleMap
    private var sbNetwork: Snackbar? = null
    private val bottomSheet by lazy {
        BottomSheetBehavior.from(clBottomSheet)
    }
    private val userAdapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        subscribeDataChange()
        viewModel.getUsers()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun displayLoading(isLoading: Boolean) {
        cpbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        viewLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun initViews() {
        initMap()
        initUserRecyclerView()
        setTouchEffect()
        setClickListener()
        setCallbackListener()
    }

    private fun initUserRecyclerView() {
        with(rvUsers) {
            layoutManager = LinearLayoutManager(baseContext)
            adapter = userAdapter
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)
    }

    private fun setTouchEffect() {
        viewOpenBar.setOnTouchListener { _, motionEvent ->
            openBar.onTouchEvent(motionEvent)
            false
        }
    }

    private fun setClickListener() {
        viewOpenBar.setOnClickListener {
            if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            } else if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun setCallbackListener() {
        userAdapter.setOnItemClickListener {
            userAdapter.updateSelectUser(it)
        }
    }

    private fun showRetry() {
        sbNetwork =
            Snackbar.make(clBottomSheetContent, R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE).setAction(
                R.string.retry
            ) {
                viewModel.getUsers()
            }
        sbNetwork!!.show()
    }

    private fun subscribeDataChange() {
        subscribeNetworkState(viewModel.isConnectedNetwork)
        subscribeUsers(viewModel.users)
        subscribeApiError(viewModel.apiError)
        subscribeLoadingEvent(viewModel.isLoading)
    }

    private fun subscribeNetworkState(state: Observable<Boolean>) {
        state
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { isConnected ->
                if (isConnected) sbNetwork?.dismiss()
                else showRetry()
            }
            .subscribe()
            .disposeOnDestroy()
    }

    private fun subscribeUsers(state: Observable<List<User>>) {
        state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userAdapter.update(it)
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

    private fun subscribeApiError(state: Observable<Throwable>) {
        state
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { }
            .subscribe()
            .disposeOnDestroy()
    }

    companion object {

        fun startActivity(activity: BaseActivity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
