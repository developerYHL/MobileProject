package com.example.mobileproject;

public class ClusterItemManager  {

    private static final ClusterItemManager instance = new ClusterItemManager();

    private ClusterItemManager() {}

    public static ClusterItemManager getInstance() {
        return instance;
    }


}
