package com.lx862.pwgui.util;

import com.lx862.pwgui.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHelper {
    public static String getFromURL(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Main.LOGGER.info(String.format("%s request to %s", connection.getRequestMethod(), url));
        int responseCode = connection.getResponseCode();
        Main.LOGGER.info(String.format("Got HTTP %d for %s", responseCode, url));
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder resp = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            resp.append(line).append("\n");
        }
        br.close();
        return resp.toString();
    }
}
