package com.example.mobileproject.holder;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;

import java.util.Collection;

public class ClusterItemHolder implements Cluster {

    public final String name;
    public final Bitmap profilePhoto;
    private final LatLng mPosition;

    public ClusterItemHolder(LatLng position, String name, Bitmap pictureResource) {
        this.name = name;
        profilePhoto = pictureResource;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public Collection getItems() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }


}
