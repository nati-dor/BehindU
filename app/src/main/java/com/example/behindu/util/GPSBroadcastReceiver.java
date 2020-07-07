package com.example.behindu.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.example.behindu.viewmodel.ChildViewModel;


public class GPSBroadcastReceiver extends BroadcastReceiver {

    private ChildViewModel viewModel = new ChildViewModel();

    @Override
    public void onReceive(Context context, Intent intent) {

        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                viewModel.isGPSOn(true);
            } else
                viewModel.isGPSOn(false);


            Log.d("TAG", "onReceive: Arrive");
        }
    }
}
