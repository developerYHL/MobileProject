package com.example.mobileproject.fragments

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyItem(lat: Double, lng: Double) : ClusterItem {
    private val mPosition: LatLng

    init {
        mPosition = LatLng(lat, lng)
    }

    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return null
    }
}
