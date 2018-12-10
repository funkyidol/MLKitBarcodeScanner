package com.funkyidol.mlkitbarcodescanner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

class PermissionsHelper {

    private static final int REQUEST_CODE = 10;
    private final Activity activity;

    PermissionsHelper(Activity activity) {
        this.activity = activity;
    }

    boolean hasCameraPermission() {
        int permissionCheckResult = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
        );
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED;
    }

    void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE
        );
    }

    boolean resultGranted(int requestCode,
                          String[] permissions,
                          int[] grantResults) {

        if (requestCode != REQUEST_CODE) {
            return false;
        }

        if (grantResults.length < 1) {
            return false;
        }
        if (!(permissions[0].equals(Manifest.permission.CAMERA))) {
            return false;
        }


        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestCameraPermission();
        return false;
    }
}