package com.use.myhaha;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrength;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.R)
public class TelephonyManagerModuleActivity extends AppCompatActivity {

    private static final String TAG = "TelephonyManagerModule";
    private static TelephonyManager tm;

    private PhoneStateListener psl;
    private int events = PhoneStateListener.LISTEN_NONE;

    private final int READ_PHONE_STATE_PERMISSION_CODE = 101;
    private final int ACCESS_FINE_LOCATION_PERMISSION_CODE = 102;
    private final int READ_PRECISE_PHONE_STATE_PERMISSION_CODE = 104;



    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephony_manager_module);

        checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_PERMISSION_CODE);
        checkPermission(Manifest.permission.READ_PRECISE_PHONE_STATE, READ_PRECISE_PHONE_STATE_PERMISSION_CODE);

        getManager();
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_INFO);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        try {
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_PRECISE_DATA_CONNECTION_STATE);
        }catch (SecurityException se){
            Log.e(TAG, "SecurityException se " + se.getLocalizedMessage());
        }

        getNetwork();
        getSim();

    }

    private void getManager() {
        if (tm != null) return;

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    @RequiresPermission(android.Manifest.permission.READ_PRECISE_PHONE_STATE)
    private final PhoneStateListener phoneStateListener =
            new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    // Handle call state change
                    Log.e(TAG, "onCallStateChanged state, phoneNumber " + state);

                }

                @Override
                public void onCellInfoChanged(List<CellInfo> cellInfo) {
                    Log.e(TAG, "onCellInfoChanged cellInfo " + cellInfo);

                }

                @Override
                public void onCellLocationChanged(CellLocation location) {
                    Log.e(TAG, "onCellLocationChanged location " + location);
                }

                @Override
                public void onDataConnectionStateChanged(int state, int networkType) {
                    Log.e(TAG, "onDataConnectionStateChanged state " + state + " networkType " + networkType);

                }

                @Override
                public void onServiceStateChanged(ServiceState serviceState) {
                    Log.e(TAG, "onServiceStateChanged serviceState " + serviceState);
                }

                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    Log.e(TAG, "onSignalStrengthsChanged signalStrength " + signalStrength);
                    Log.e(TAG, "getStrengthSignal Level " + getStrengthSignal(signalStrength.getLevel()));


                }

                @Override
                public void onPreciseDataConnectionStateChanged(@NonNull PreciseDataConnectionState dataConnectionState) {
                    Log.e(TAG, "onPreciseDataConnectionStateChanged dataConnectionState " + dataConnectionState);
                    Log.e(TAG, "onPreciseDataConnectionStateChanged mFailCause " + dataConnectionState.getLastCauseCode());
                }
            };

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public void getNetwork() {
        Log.e(TAG, "getNetworkCountryIso " + tm.getNetworkCountryIso());
        Log.e(TAG, "getNetworkOperator " + tm.getNetworkOperator());
        Log.e(TAG, "getNetworkOperatorName " + tm.getNetworkOperatorName());

        Log.e(TAG, "getNetworkType " + tm.getNetworkType());
        Log.e(TAG, "getPhoneType " + tm.getPhoneType());

    }

    private void getSim(){
        Log.e(TAG, "getSimCountryIso " + tm.getSimCountryIso());
        Log.e(TAG, "getSimOperator " + tm.getSimOperator());
        Log.e(TAG, "getSimOperatorName " + tm.getSimOperatorName());

    }

    String getStrengthSignal(int level) {
        switch (level) {
            case CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN:
                return "SIGNAL_STRENGTH_NONE_OR_UNKNOWN";
            case CellSignalStrength.SIGNAL_STRENGTH_POOR:
                return "SIGNAL_STRENGTH_POOR";
            case CellSignalStrength.SIGNAL_STRENGTH_MODERATE:
                return "SIGNAL_STRENGTH_MODERATE";
            case CellSignalStrength.SIGNAL_STRENGTH_GOOD:
                return "SIGNAL_STRENGTH_GOOD";
            case CellSignalStrength.SIGNAL_STRENGTH_GREAT:
                return "SIGNAL_STRENGTH_GREAT";
        }
        return "UNKNOWN";
    }


    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(TelephonyManagerModuleActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(TelephonyManagerModuleActivity.this, new String[] { permission }, requestCode);
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


        if (requestCode == READ_PHONE_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(TelephonyManagerModuleActivity.this, "Phone State Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TelephonyManagerModuleActivity.this, "Phone State Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(TelephonyManagerModuleActivity.this, "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TelephonyManagerModuleActivity.this, "Fine Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == READ_PRECISE_PHONE_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(TelephonyManagerModuleActivity.this, "Precise phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TelephonyManagerModuleActivity.this, "Precise phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}