package com.example.mobileproject.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mobileproject.R;
import com.example.mobileproject.model.ClusterItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NavigationFragment extends BaseDemoActivity implements ClusterManager.OnClusterClickListener<ClusterItem>, ClusterManager.OnClusterInfoWindowClickListener<ClusterItem>, ClusterManager.OnClusterItemClickListener<ClusterItem>, ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItem>  {
    private ClusterManager<ClusterItem> mClusterManager;
    private Random mRandom = new Random(1984);
    FirebaseFirestore db;
    private FirebaseUser mUser;
    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class ClusterItemRenderer extends DefaultClusterRenderer<ClusterItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;


        public ClusterItemRenderer() {
            super(getActivity(), getMap(), mClusterManager);


Log.e("QQQ","ClusterItemRenderer");

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getActivity());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterItem person, MarkerOptions markerOptions) {
            Log.e("QQQ","onBeforeClusterItemRendered");
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageBitmap(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<ClusterItem> cluster, MarkerOptions markerOptions) {
            Log.e("QQQ","onBeforeClusterRendered");
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (ClusterItem p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = new BitmapDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            Log.e("QQQ","shouldRenderAsCluster");

            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<ClusterItem> cluster) {

        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(getActivity(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (com.google.maps.android.clustering.ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<ClusterItem> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(ClusterItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    @Override
    protected void startDemo() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        queryData();
        //queryData();
    }

    private void addItems(String markerName, String ImageUrl, GeoPoint geoPoint) {
        Log.d("#@!", markerName+ " => " + geoPoint);
        //getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9.5f));

        mClusterManager = new ClusterManager<ClusterItem>(getActivity(), getMap());
        mClusterManager.setRenderer(new ClusterItemRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

        Glide.with(getActivity())
                .asBitmap()
                .load(ImageUrl)
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mClusterManager.addItem(new ClusterItem(latLng, markerName, resource));
                    }
                });

        mClusterManager.cluster();
    }

    private  void queryData(){
        // Create a reference to the cities collection
        CollectionReference citiesRef = db.collection("post");

        // Create a query against the collection.
        Query query = citiesRef.whereEqualTo("uid", true);

        Log.e("query", "query : " + query);

        db.collection("post")
                .whereEqualTo("uid", mUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //
                                //, document.getString("downloadUrl"), (LatLng) document.get("latlan")
                                addItems(document.getString("uid"), document.getString("downloadUrl"), document.getGeoPoint("geopoint"));
                                Log.d("!@#", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("!@#", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private LatLng position(LatLng latLng) {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
