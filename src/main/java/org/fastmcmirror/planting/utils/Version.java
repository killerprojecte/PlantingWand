package org.fastmcmirror.planting.utils;

import org.fastmcmirror.planting.PlantingWand;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Version {
    public static void checkUpdate() {
        StringBuilder sb = new StringBuilder();
        String str;
        try {
            URL url = new URL("https://fastmcmirror.org/flybuff-dev.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8));
            while ((str = reader.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            sb.delete(sb.length() - 1, sb.length());
            if (sb.toString().equalsIgnoreCase(PlantingWand.instance.getDescription().getVersion())) {
                PlantingWand.instance.getLogger().info("You are using the latest PlantingWand!");
            } else {
                PlantingWand.instance.getLogger().warning("PlantingWand has a new update! You are using version " + PlantingWand.instance.getDescription().getVersion() + " . The latest version is " + sb.toString() + "!");
            }
        } catch (Exception e) {
            PlantingWand.instance.getLogger().severe("Failed to get online version-information: " + e.getMessage());
        }
    }
}
