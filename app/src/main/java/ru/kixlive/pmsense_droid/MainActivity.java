package ru.kixlive.pmsense_droid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "PMSenseDroid";
    private BluetoothAdapter mBluetoothAdapter;
    private LeDeviceListAdapter mDeviceListAdapter;
    private RecyclerView mRecyclerView;
    private Button mScanButton;
    private Handler mHandler;
    private boolean mScanning;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SCAN_PERIOD = 10000;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_device_scan);

        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_devices);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mDeviceListAdapter);

        mScanButton = (Button) findViewById(R.id.scan_button);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mDeviceListAdapter = new LeDeviceListAdapter(new ArrayList<BluetoothDevice>()) {
            @Override
            public void onClick(View view) {
                super.onClick(view);

                final BluetoothDevice device = (BluetoothDevice) view.getTag();
                if (device == null) return;

                Log.v(TAG, device.getName());
                Log.v(TAG, device.getAddress());
            }
        };
        mRecyclerView.setAdapter(mDeviceListAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                .build();

        BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        ScanFilter filter = new ScanFilter.Builder()
                .setDeviceName("PMSense")
                .build();

        filters.add(filter);

        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.v(TAG, "Stopping scan");
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            Log.v(TAG, "Starting scan");
            bluetoothLeScanner.startScan(filters, scanSettings, mLeScanCallback);
        } else {
            mScanning = false;
            Log.v(TAG, "Stopping scan");
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    private final ScanCallback mLeScanCallback = new ScanCallback() {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Scan failed: "+errorCode);
            super.onScanFailed(errorCode);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            Log.v(TAG, "Found device "+result.getDevice().getAddress());
            mDeviceListAdapter.add(result.getDevice());
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, yay! Start the Bluetooth device scan.
                    scanLeDevice(true);
                } else {
                    // Alert the user that this application requires the location permission to perform the scan.
                }
            }
        }
    }
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.v(TAG, "Adding device "+device.getName());
//                            mDeviceListAdapter.add(device);
//                        }
//                    });
//                }
//            };
}