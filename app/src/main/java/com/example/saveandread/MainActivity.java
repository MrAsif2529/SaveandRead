package com.example.saveandread;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText editText;
    private TextView textView;
    private static final int MULTI_PERMISSION_REQUEST_CODE = 123;
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 124;
    private static final int REQUEST_WIFI_STATE_PERMISSION = 123;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    private static final int MANAGE_EXTERNAL_STORAGE_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request permissions
        checkAndRequestPermissions();

//        Information.checkNIC();

//        String macAddressEth0 = Information.getMacAddress("eth0");
//        Log.d(TAG, "MAC Address for eth0: " + macAddressEth0);
//
//        String macAddressEth1 =Information.getMacAddress("eth1");
//        Log.d(TAG, "MAC Address for eth1: " + macAddressEth1);
//
//        // Get MAC address for wlan0
//        String macAddressWlan0 = Information.getMacAddress("wlan0");
//        Log.d(TAG, "MAC Address for wlan0: " + macAddressWlan0);
//
//        // Get MAC address for dummy0
//        String macAddressDummy0 = Information.getMacAddress("dummy0");
//        Log.d(TAG, "MAC Address for dummy0: " + macAddressDummy0);
//
//        String macAddressIp6_vti0 =Information.getMacAddress("ip6_vti0");
//        Log.d(TAG, "MAC Address for ip6_vti0: " + macAddressIp6_vti0);
//
//        String macAddressCan0 =Information.getMacAddress("can0");
//        Log.d(TAG, "MAC Address for can0: " + macAddressCan0);
//
//        String macAddressIp6tnl0 =Information.getMacAddress("ip6tnl0");
//        Log.d(TAG, "MAC Address for ip6tnl0: " + macAddressIp6tnl0);
//
//        String macAddressIp_vti0 =Information.getMacAddress("ip_vti0");
//        Log.d(TAG, "MAC Address for ip_vti0: " + macAddressIp_vti0);
//
//        String macAddressLo =Information.getMacAddress("lo");
//        Log.d(TAG, "MAC Address for lo: " + macAddressLo);
//
//        String macAddressSit0 =Information.getMacAddress("sit0");
//        Log.d(TAG, "MAC Address for sit0: " + macAddressSit0);
//
        List<String> nics = NicManager.getNics();
        Log.d(TAG, "NICs: " + nics.toString());

        String nic = "eth0"; // Example NIC key
        String macAddress = NicManager.getMac(nic);
        System.out.println("MAC Address for " + nic + ": " + macAddress);
        Log.d(TAG,"MAC Address for " +nic + ":"+macAddress);



        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        Button saveButton = findViewById(R.id.save_button);
        Button readButton = findViewById(R.id.open_log);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            } else {
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]),
                    MULTI_PERMISSION_REQUEST_CODE);
        }

        // Check if MANAGE_EXTERNAL_STORAGE permission is required (for Android 11+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                    MANAGE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }
    private int getRequestCode(String permission) {
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return WRITE_EXTERNAL_STORAGE_REQUEST_CODE;
            case Manifest.permission.ACCESS_WIFI_STATE:
                return REQUEST_WIFI_STATE_PERMISSION;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return ACCESS_FINE_LOCATION_REQUEST_CODE;
            default:
                return -1; // Return a default value if permission is not recognized
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MANAGE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now save and read files
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied. Unable to save and read files.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void saveData() {
        String text = editText.getText().toString();
        if (!text.isEmpty()) {
            boolean success = saveToFile("saved_text.txt", text);
            if (success) {
                editText.setText("");
                Toast.makeText(this, "Data has been saved to Device", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter some text to save", Toast.LENGTH_SHORT).show();
        }
    }

    public void readData() {
        String filePath = "saved_text.txt";
        File file = new File(Environment.getExternalStorageDirectory(), filePath);
        if (file.exists()) {
            String text = readFromFile(new File(String.valueOf(file)));
            if (text != null) {
                textView.setText("Saved Text:\n" + text);
            } else {
                Toast.makeText(this, "Failed to read file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveToFile(String filename, String data) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.close();
            return true; // Save successful
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Save failed
        }
    }

    private String readFromFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            fis.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null to indicate failure to read the file
        }
    }
}