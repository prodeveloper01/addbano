package com.example.newfestivalpost.Notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.newfestivalpost.Activities.TermsActivity;
import com.example.newfestivalpost.NewData.NewActivity.ActivitySingleCategoyList1;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationClickHandler implements OneSignal.OSNotificationOpenedHandler {
    Context context2;

    public NotificationClickHandler(Context context) {
        context2 = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenedResult result) {
        OSNotificationAction.ActionType actionType = result.getAction().getType();

        JSONObject data = result.getNotification().getAdditionalData();
        String customKey;
        String id = null;
        String type = null;
        String openType = null;
        String webUrl = null;

        try {
            id = data.getString("id");
            type = data.getString("vtype");
            openType = data.getString("open");
            webUrl = data.getString("url");
            Log.d("noti:", data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (openType.equalsIgnoreCase("web")) {

            Intent intent = new Intent(context2, TermsActivity.class);
            intent.putExtra("url", webUrl);
            intent.putExtra("title", result.getNotification().getTitle());
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context2.startActivity(intent);

        } else {
            Intent intent = new Intent(context2, ActivitySingleCategoyList1.class);
            intent.putExtra("id", id);
            intent.putExtra("vType", type);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context2.startActivity(intent);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.getAction().getActionId());
        }
    }
}
