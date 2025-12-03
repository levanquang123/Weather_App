package com.quang.weatherclient.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {

    public static String getWeatherByLatLon(double lat, double lon) throws Exception {

        String apiUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f"
                        + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,"
                        + "cloud_cover,pressure_msl,visibility,weather_code,wind_speed_10m"
                        + "&daily=temperature_2m_max,temperature_2m_min,weather_code,sunrise,sunset"
                        + "&forecast_days=7"
                        + "&timezone=auto",
                lat, lon
        );

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) sb.append(line);

        br.close();
        return sb.toString();
    }
}
