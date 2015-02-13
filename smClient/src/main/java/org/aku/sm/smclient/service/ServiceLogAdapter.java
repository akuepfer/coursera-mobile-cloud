package org.aku.sm.smclient.service;

import android.util.Log;

import retrofit.RestAdapter;

/**
 * logging for debug messages, avoid large trace for binary data
 */
public class ServiceLogAdapter implements RestAdapter.Log {
    @Override
    public void log(String message) {
        if (message.length() > 5000)
        Log.d("Retrofit", "Large message, length " + message.length() + " " + message.substring(0, 20));
    }
}

