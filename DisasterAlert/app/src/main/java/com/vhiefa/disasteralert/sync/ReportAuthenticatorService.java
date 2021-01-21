package com.vhiefa.disasteralert.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Afifatul on 4/11/2017.
 */

public class ReportAuthenticatorService  extends Service{
    private ReportAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new ReportAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
