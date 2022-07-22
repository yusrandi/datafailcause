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
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.DataFailCause;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TelephonyManager _teTelephonyManager;
    private NetworkCapabilities networkCapabilities;
    private CellSignalStrength cellSignalStrength;

    private final int CAMERA_PERMISSION_CODE = 100;
    private final int READ_PHONE_STATE_PERMISSION_CODE = 101;
    private final int ACCESS_FINE_LOCATION_PERMISSION_CODE = 102;
    private final int ACCESS_NETWORK_STATE_PERMISSION_CODE = 103;
    private final int READ_PRECISE_PHONE_STATE_PERMISSION_CODE = 104;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, ACCESS_NETWORK_STATE_PERMISSION_CODE);
        checkPermission(Manifest.permission.READ_PRECISE_PHONE_STATE, READ_PRECISE_PHONE_STATE_PERMISSION_CODE);

        _teTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            networkCapabilities = new NetworkCapabilities();
            Log.e(TAG, "getSignalStrength " + _teTelephonyManager.getSignalStrength());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.e(TAG, "isDataEnabled " + _teTelephonyManager.isDataEnabled());
            }
            Log.e(TAG, "networkCapabilities " + networkCapabilities.getSignalStrength());
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
        int upSpeed = nc.getLinkUpstreamBandwidthKbps();

        Log.e(TAG, "downspeed " + downSpeed + " upSpeed " + upSpeed);


        SignalStrength ss = _teTelephonyManager.getSignalStrength();


        Log.e(TAG, "Level Signal " + getStrengthSignal(ss.getLevel()));
        Log.e(TAG, "signal strength " + cellSignalStrength(_teTelephonyManager));

//        DataFailCause

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dataFailCauseTes();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    void dataFailCauseTes(){
        int mFailCause;

        int listenPreciseDataConnectionState = PhoneStateListener.LISTEN_PRECISE_DATA_CONNECTION_STATE;
        Log.e(TAG, "listenPreciseDataConnectionState " + listenPreciseDataConnectionState);


        registerCallStateListener();

    }

    private void registerCallStateListener() {
        if (!callStateListenerRegistered) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.e(TAG, "callStateListener");

                telephonyManager.registerTelephonyCallback(getMainExecutor(), (TelephonyCallback) preciseDataConnectionStateListener);
                callStateListenerRegistered = true;

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    telephonyManager.registerTelephonyCallback(getMainExecutor(), callStateListener);
                    callStateListenerRegistered = true;
                }

            } else {
                Log.e(TAG, "phoneStateListener");

                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                callStateListenerRegistered = true;
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private static abstract class CallStateListener extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        @Override
        abstract public void onCallStateChanged(int state);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private static abstract class PreciseDataConnectionStateListener extends TelephonyCallback implements TelephonyCallback.PreciseDataConnectionStateListener {
        @Override
        abstract public void  onPreciseDataConnectionStateChanged(PreciseDataConnectionState dataConnectionState);
    }

    private boolean callStateListenerRegistered = false;

    private CallStateListener callStateListener = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
            new CallStateListener() {
                @Override
                public void onCallStateChanged(int state) {
                    // Handle call state change
                    Log.e(TAG, "onCallStateChanged state " + state);


                }
            }
            : null;

    private PreciseDataConnectionStateListener preciseDataConnectionStateListener = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
           new PreciseDataConnectionStateListener() {
               @Override
               public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState dataConnectionState) {
                   Log.e(TAG, "preciseDataConnectionStateListener dataConnectionState " + dataConnectionState);
               }
           }
            : null;

    private PhoneStateListener phoneStateListener = (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) ?
            new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    // Handle call state change
                    Log.e(TAG, "onCallStateChanged state, phoneNumber " + state+" , "+phoneNumber);

                }
            }
            : null;


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

    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    String cellSignalStrength(TelephonyManager tm) {
        String gsmStrength = "";
        try {



            gsmStrength = "getAllCellInfo " + tm.getAllCellInfo();

            for (CellInfo info : tm.getAllCellInfo()) {
                if (info instanceof CellInfoGsm) {
                    CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                    // do what you need
                    gsmStrength = String.valueOf(gsm.getDbm());
                } else if (info instanceof CellInfoCdma) {
                    CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                    gsmStrength = String.valueOf(cdma.getDbm());
                } else if (info instanceof CellInfoLte) {
                    CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                    gsmStrength = String.valueOf(lte.getDbm());
                } else {
                    gsmStrength = String.valueOf("Uknow");
                }
            }


        }catch (Exception e){
            return e.getMessage();
        }

        return gsmStrength;
    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
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

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == READ_PHONE_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Phone State Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Phone State Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Fine Location Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Fine Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == ACCESS_NETWORK_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Network State Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Network State Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == READ_PRECISE_PHONE_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Precise Phone State Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Precise Phone State Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



}