package com.cartrack.assignment.ui.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cartrack.assignment.R
import com.cartrack.assignment.data.bean.User
import com.cartrack.assignment.ui.base.BaseActivity
import com.cartrack.assignment.ui.main.adapter.UserAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.cpbLoading
import kotlinx.android.synthetic.main.activity_login.viewLoading
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sub_bottom_sheet.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var mMap: GoogleMap
    private var sbNetwork: Snackbar? = null
    private val bottomSheet by lazy {
        BottomSheetBehavior.from(clBottomSheet)
    }
    private val userAdapter by lazy { UserAdapter(rvUsers) }
    private var markers = mutableListOf<UserMarker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        subscribeDataChange()
        viewModel.getUsers()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        googleMap.uiSettings.isMyLocationButtonEnabled = false
    }

    override fun onMarkerClick(mark: Marker): Boolean {
        markers.forEach {
            if (it.mark == mark) {
                it.mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.select_user))
                it.mark.showInfoWindow()
                userAdapter.updateSelectUser(it.user)
            }
            else {
                it.mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.unselect_user))
                it.mark.hideInfoWindow()
            }
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mark.position, 5F))
        return true
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
        viewExpand.setOnTouchListener { _, motionEvent ->
            imgExpand.onTouchEvent(motionEvent)
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
        viewExpand.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(1f))
        }
    }

    private fun setCallbackListener() {
        userAdapter.setOnItemClickListener {clickedUser ->
            //userAdapter.updateSelectUser(clickedUser)
            markers.forEach {
                if(it.user == clickedUser){
                    onMarkerClick(it.mark)
                }
            }
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

    @SuppressLint("ObjectAnimatorBinding")
    private fun showMarker(data: List<User>) {
        data.map {
            val laLng = LatLng(it.address.geo.lat.toDouble(), it.address.geo.lng.toDouble())
            val markerOptions = MarkerOptions().position(laLng)
            val marker = mMap.addMarker(markerOptions)
            marker.title = it.userName
            markers.add(UserMarker(it , marker))
            addMarkerAnimation(marker)
        }
    }

    private fun addMarkerAnimation(marker: Marker) {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.unselect_user)
        val animation = ValueAnimator.ofFloat(0.1F, 0.8F)
        animation.duration = 500
        animation.interpolator = AnticipateOvershootInterpolator()

        animation.addUpdateListener { animationState ->
            val scaleFactor = animationState.animatedValue as Float
            val newBitMap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scaleFactor).toInt(),
                (bitmap.height * scaleFactor).toInt(),
                false
            )
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(newBitMap))
        }
        animation.start()
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
                showMarker(it)
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

data class UserMarker(val user : User ,val mark: Marker)