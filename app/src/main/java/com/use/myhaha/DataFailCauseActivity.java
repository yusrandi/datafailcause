package com.use.myhaha;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class DataFailCauseActivity extends AppCompatActivity {

    private static final String TAG = "DataFailCauseActivity";
    private boolean callStateListenerRegistered = false;

    private final int READ_PRECISE_PHONE_STATE_PERMISSION_CODE = 104;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_fail_cause);

        checkPermission(Manifest.permission.READ_PRECISE_PHONE_STATE, READ_PRECISE_PHONE_STATE_PERMISSION_CODE);

        registerCallStateListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private final PreciseDataConnectionStateListener preciseDataConnectionStateListener =
            new PreciseDataConnectionStateListener() {
                @Override
                public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState dataConnectionState) {
                    Log.e(TAG, "preciseDataConnectionStateListener dataConnectionState " + dataConnectionState);
                    Log.e(TAG, "preciseDataConnectionStateListener getDataFailCause " + dataConnectionState.getLastCauseCode());
                }
            };


    @RequiresApi(api = Build.VERSION_CODES.S)
    private static abstract class PreciseDataConnectionStateListener extends TelephonyCallback implements TelephonyCallback.PreciseDataConnectionStateListener {
        @Override
        abstract public void  onPreciseDataConnectionStateChanged(PreciseDataConnectionState dataConnectionState);
    }

    private void registerCallStateListener() {
        if (!callStateListenerRegistered) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.e(TAG, "Build.VERSION_CODES.S ");

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PRECISE_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    telephonyManager.registerTelephonyCallback(getMainExecutor(), (TelephonyCallback) preciseDataConnectionStateListener);
                    callStateListenerRegistered = true;
                }
            } else {
                Log.e(TAG, "Build.VERSION_CODES < S");
            }
        }
    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(DataFailCauseActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(DataFailCauseActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(DataFailCauseActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        Log.e(TAG, "permissions "+permissions[0]+" grantResults "+grantResults[0]);

        if (requestCode == READ_PRECISE_PHONE_STATE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(DataFailCauseActivity.this, "Read Precise Phone State Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(DataFailCauseActivity.this, "Read Precise Phone State Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }




}