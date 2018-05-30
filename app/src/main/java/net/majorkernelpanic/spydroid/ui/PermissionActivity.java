package net.majorkernelpanic.spydroid.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

public class PermissionActivity extends Activity {
    private static final int REQUEST_PERMISSION = 0x01;
    private static final int REQUEST_SETTINGS = 0x02;
    private static final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryCheckPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS) {
            tryCheckPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            for (int result : grantResults) {
                if (PackageManager.PERMISSION_GRANTED != result) {
                    showNeedPermission();
                    return;
                }
            }
            tryCheckPermissions();
        }
    }

    private void showNeedPermission() {
        new AlertDialog.Builder(this).setTitle("Note").setMessage("Need Permission!!!").setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openSettings(PermissionActivity.this, REQUEST_SETTINGS);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    private void tryCheckPermissions() {
        boolean hasRequiredPermission = true;
        for (String perm : permissions) {
            hasRequiredPermission &= checkPermission(perm);
        }
        if (hasRequiredPermission) {
            onPermissionReady();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    private void onPermissionReady() {
        startActivity(new Intent(this, SpydroidActivity.class));
        finish();
    }

    private void openSettings(Activity activity, int requestForResult) {
        // Create app settings intent
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        if (requestForResult <= 0) {
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, requestForResult);
        }
    }
}
