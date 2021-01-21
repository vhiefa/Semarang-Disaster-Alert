package com.vhiefa.disasteralert.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Afifatul on 4/11/2017.
 */

public class ReportSyncService  extends Service{
        private static final Object sSyncAdapterLock = new Object();
        private static ReportSyncAdapter sSyncAdapter = null;
        @Override
        public void onCreate() {
            synchronized (sSyncAdapterLock){
                if(sSyncAdapter == null){
                    sSyncAdapter = new ReportSyncAdapter(getApplicationContext(), true);
                }
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return sSyncAdapter.getSyncAdapterBinder();
        }
    }
