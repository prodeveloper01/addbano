package com.example.newfestivalpost;

import static com.example.newfestivalpost.Config.API_KEY;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newfestivalpost.Notification.NotificationClickHandler;
import com.example.newfestivalpost.Open_ads.AppOpenManager;
import com.example.newfestivalpost.Retrofit.Base_Url1;
import com.example.newfestivalpost.payment.Utils.PreferenceUtils;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsApplication extends Application {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    private AnalyticsApplication currentActivity;
    String google_startapp;
    AppOpenManager appOpenManager;
    public static final String NOTIFICATION_CHANNEL_ID = "download_channel_id";
    public static final String ONESIGNAL_APP_ID = "0e06a218-9156-4e49-937d-9589f9b445c3";

    String adsEnable;

    @Override
    public void onCreate() {
        super.onCreate();

        currentActivity = this;

        sAnalytics = GoogleAnalytics.getInstance(this);

        getAllData();
        createNotificationChannel();


//        OneSignal.startInit(this)
//                .setNotificationOpenedHandler(new NotificationClickHandler(currentActivity))
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();

//        SharedPreferences preferences = getSharedPreferences("push", MODE_PRIVATE);
//        if (preferences.getBoolean("status", true)) {
//            OneSignal.setSubscription(true);
//        } else {
//            OneSignal.setSubscription(false);
//        }
        OneSignal.initWithContext(AnalyticsApplication.this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.setNotificationOpenedHandler(new NotificationClickHandler(AnalyticsApplication.this));


    }


    private void getAllData() {

        StringRequest request = new StringRequest(Request.Method.GET, Base_Url1.BASE_URL + "config?API-KEY=" + API_KEY,
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        JSONObject obj = null;

                        try {

                            obj = new JSONObject(response);
                            JSONObject Object = obj.getJSONObject("ads_config");

                            google_startapp = Object.optString("admob_open_ads_id");
                            adsEnable = Object.optString("ads_enable");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(PreferenceUtils.isActivePlan(getApplicationContext())|| adsEnable.equals("0")){
                            appOpenManager = new AppOpenManager(currentActivity, "000000");
                        } else {
                            appOpenManager = new AppOpenManager(currentActivity, google_startapp);
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.e("error", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                String username = "basic";
                String password = "1234";
                String auth = new String(username + ":" + password);
                byte[] data = auth.getBytes();
                String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<String, String>();
                Log.e("base64", "getHeaders: " + base64);
                headers.put("Authorization", "Basic " + base64);
                return headers;
            }

        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NotificationName",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    synchronized public Tracker getDefaultTracker() {
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}

