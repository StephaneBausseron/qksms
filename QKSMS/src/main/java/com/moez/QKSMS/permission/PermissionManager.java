package com.moez.QKSMS.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.moez.QKSMS.QKSMSApp;
import com.moez.QKSMS.ui.base.QKActivity;



public class PermissionManager {
    public static final String LOG_TAG = "PermissionManager";

    private static PermissionManager _instance = new PermissionManager();
    public static PermissionManager getInstance() {
        return _instance;
    }

    public static class Permission {
        private final String mPermission;
        private Integer mState;
        private final boolean mMandatory;

        private Permission(String permission, boolean mandatory) {
            mPermission = permission;
            mMandatory = mandatory;
            mState = null;
        }

        public boolean isGranted() {
            // Return false if permission is DENIED ou unknown
            return mState == PackageManager.PERMISSION_GRANTED;
        }
    }

    private static final String[] mMandatoryPermissions = new String[] {
            Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.CHANGE_NETWORK_STATE,
            "android.permission.FLASHLIGHT",
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.INTERNET,
            "android.permission.MANAGE_ACCOUNTS",
            Manifest.permission.NFC,
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN?Manifest.permission.READ_CALL_LOG:null),
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            "android.permission.READ_PROFILE",
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN?Manifest.permission.READ_EXTERNAL_STORAGE:null),
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            "android.permission.RECEIVE_MMS",
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SEND_SMS,
            "android.permission.USE_CREDENTIALS",
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "android.permission.WRITE_SMS",
    };
    private static final String[] mAllPermissions = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.CHANGE_CONFIGURATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            "android.permission.FLASHLIGHT",
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.INTERNET,
            "android.permission.MANAGE_ACCOUNTS",
            Manifest.permission.NFC,
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN?Manifest.permission.READ_CALL_LOG:null),
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            "android.permission.READ_PROFILE",
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN?Manifest.permission.READ_EXTERNAL_STORAGE:null),
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            "android.permission.RECEIVE_MMS",
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SEND_SMS,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            "android.permission.USE_CREDENTIALS",
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "android.permission.WRITE_SMS", // WRITE_SMS removed since API 23
            "android.permission.BILLING",
    };

    private PermissionManager() {

    }

    private boolean mAllMandatoryPermissionsGranted = false;
    public boolean isAllMandatoryPermissionsAreGranted() {
        return mAllMandatoryPermissionsGranted;
    }
    public void refreshAllMandatoryPermissionsGranted(@NonNull Context context) {
        Log.i(LOG_TAG, "Recheck if all mandatory permissions are granted");
        boolean result = true;
        for(String permission: mMandatoryPermissions) {
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.i(LOG_TAG, "Mandatory permission '" + permission + "' is DENIED");
                result = false;
            } else {
                Log.i(LOG_TAG, "Mandatory permission '" + permission + "' is GRANTED");
            }
        }
        mAllMandatoryPermissionsGranted = result;

        if(isAllMandatoryPermissionsAreGranted()) {
            // Modules which are not initialized due to a missing permissions can be initialized now.
            QKSMSApp.getApplication().init();
        }
    }
    public boolean isMandatoryPermission(String permission) {
        for(String currentPerm:mMandatoryPermissions) {
            if(currentPerm != null && currentPerm.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    private static final int REQUEST_MANDATORY_PERMISSION = 100;
    public void requestForMandatoryPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, mMandatoryPermissions, REQUEST_MANDATORY_PERMISSION);
    }

    public boolean handleOnRequestPermissionsResult(QKActivity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_MANDATORY_PERMISSION) {
            Log.i(LOG_TAG, "Handle onRequestPermissionsResult for mandatory permissions");
            for (int i = 0; i < permissions.length; i++) {
                if (isMandatoryPermission(permissions[i])) {
                    // Ignore optional permission here
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // User refuse a mandatory permission.
                        Log.i(LOG_TAG, "Permission '" + permissions[i] + "' DENIED by user");
                        return false;
                    }
                }
            }
        }

        refreshAllMandatoryPermissionsGranted(activity);
        return true;
    }

    public void showConfigurationPanelForDeniedMandatoryPermission(@NonNull Activity activity) {
        if (!isAllMandatoryPermissionsAreGranted()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        }
    }
}
