package com.company.millenium.poopficiton;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by chavdar on 12/20/15.
 */
public class DetectConnection {
    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}