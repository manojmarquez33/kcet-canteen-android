package com.kcet.canteen.activity;

import com.onesignal.OneSignal;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);

    }
}
