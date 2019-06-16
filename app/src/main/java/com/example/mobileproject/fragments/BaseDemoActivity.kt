package com.example.mobileproject.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.mobileproject.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

abstract class BaseDemoActivity : Fragment(), OnMapReadyCallback {
    protected var map: GoogleMap? = null
        private set


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        setUpMap()
        return view
    }

    override fun onResume() {
        super.onResume()
        setUpMap()
        startDemo()
    }

    override fun onMapReady(map: GoogleMap) {
        if (this.map != null) {
            return
        }
        this.map = map
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    /**
     * Run the demo-specific code.
     */
    protected abstract fun startDemo()
}
