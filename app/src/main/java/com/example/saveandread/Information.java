package com.example.saveandread;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Information {
    private static final String TAG = "Information";

// can0  dummy0  eth0  eth1  ip6_vti0  ip6tnl0  ip_vti0  lo  sit0  wlan0
public static String getMacAddress(String interfaceName) {
    String macAddress = "Not able to read the MAC address";
    BufferedReader br = null;
    try {
        br = new BufferedReader(new FileReader("/sys/class/net/" + interfaceName + "/address"));
        macAddress = br.readLine().toUpperCase();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return macAddress;
}
}