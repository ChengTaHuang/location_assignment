package com.cartrack.assignment.ui.main

import android.content.Intent
import android.os.Bundle
import com.cartrack.assignment.R
import com.cartrack.assignment.ui.base.BaseActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.sub_bottom_sheet.*

class MainActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val bottomSheet by lazy {
        BottomSheetBehavior.from(clBottomSheet)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun initViews() {

        initMap()
        setTouchEffect()
        setClickListener()
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

    companion object {

        fun startActivity(activity: BaseActivity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
