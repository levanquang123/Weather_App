package com.quang.weatherclient.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeocodingFetcher {

    public static double[] searchCity(String city) throws Exception {

        // ENCODE city name để hỗ trợ khoảng trắng
        String encodedCity = java.net.URLEncoder.encode(city, "UTF-8");

        String urlStr = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity;
        URL url = new URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) sb.append(line);

        br.close();

        String result = sb.toString().trim();

        // Nếu không phải JSON → lỗi → trả null
        if (!result.startsWith("{")) {
            return null;
        }

        JSONObject obj = new JSONObject(result);
        JSONArray results = obj.optJSONArray("results");

        if (results == null || results.length() == 0) {
            return null;
        }

        JSONObject first = results.getJSONObject(0);

        return new double[]{
                first.getDouble("latitude"),
                first.getDouble("longitude")
        };
    }
}
