package com.moez.QKSMS.ui.permission;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.moez.QKSMS.R;
import com.moez.QKSMS.permission.PermissionManager;
import com.moez.QKSMS.ui.base.QKActivity;
import com.moez.QKSMS.ui.view.RobotoTextView;

public class MissingPermissionActivity extends QKActivity {
    public final static int MISSING_PERMISSION_REQUEST_CODE = 35421;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionManager.getInstance().refreshAllMandatoryPermissionsGranted(this);
        if (PermissionManager.getInstance().isAllMandatoryPermissionsAreGranted()) {
            finish();
        }

        setContentView(R.layout.activity_missing_permission);

        RobotoTextView grant = (RobotoTextView) findViewById(R.id.missigin_permission_grant);
        RobotoTextView leave = (RobotoTextView) findViewById(R.id.missigin_permission_grant);

        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionManager.getInstance().requestForMandatoryPermission(MissingPermissionActivity.this);
            }
        });
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        PermissionManager.getInstance().refreshAllMandatoryPermissionsGranted(this);
        if (PermissionManager.getInstance().isAllMandatoryPermissionsAreGranted()) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }


}