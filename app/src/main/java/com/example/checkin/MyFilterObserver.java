package com.example.checkin;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class MyFilterObserver implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        // Perform actions when the lifecycle owner is created
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        // Perform actions when the lifecycle owner starts
    }

    // ... other lifecycle event methods
}