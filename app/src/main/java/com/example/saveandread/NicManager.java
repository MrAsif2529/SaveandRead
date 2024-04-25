package com.example.saveandread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NicManager {

    public static List<String> getNics() {
        List<String> nics = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/dev"));
            String line;
            br.readLine();
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.contains(":")) {
                    String nic = line.split(":")[0].trim();
                    nics.add(nic);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nics;
    }

    public static String getMac(String nic) {
        List<String> nics = getNics();
        String macAddress = "Not able to read the MAC address";

        if (!nics.contains(nic)) {
            return "NIC " + nic + " does not exist, please pass valid NIC key among: " + nics;
        }

        try {
            File file = new File("/sys/class/net/" + nic + "/address");
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                macAddress = br.readLine().toUpperCase();
                br.close();
            } else {
                macAddress = "NIC " + nic + " does not exist";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macAddress;
    }
}