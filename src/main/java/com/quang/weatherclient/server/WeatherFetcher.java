package com.quang.weatherclient.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class WeatherFetcher {

    private static final HashMap<String, double[]> coords = new HashMap<>();

    static {
        coords.put("DANANG", new double[]{16.0678, 108.2208});
        coords.put("HANOI", new double[]{21.0285, 105.8542});
        coords.put("HCM", new double[]{10.8231, 106.6297});
    }

    public static String getWeather(String city) throws Exception {

        double[] c = coords.get(city);

        String apiUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f"
                        + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,"
                        + "cloud_cover,pressure_msl,visibility,weather_code,wind_speed_10m"
                        + "&daily=sunrise,sunset&timezone=Asia/Ho_Chi_Minh",
                c[0], c[1]
        );

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);

        br.close();
        return sb.toString(); // trả JSON gốc
    }
}
