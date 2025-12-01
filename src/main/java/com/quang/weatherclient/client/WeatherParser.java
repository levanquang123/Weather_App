package com.quang.weatherclient.client;

import org.json.JSONObject;

public class WeatherParser {

    public static WeatherData parse(String json) {

        JSONObject obj = new JSONObject(json);
        JSONObject current = obj.getJSONObject("current");
        JSONObject daily = obj.getJSONObject("daily");

        WeatherData data = new WeatherData();

        data.temperature  = current.getDouble("temperature_2m") + " °C";
        data.realFeel     = current.getDouble("apparent_temperature") + " °C";
        data.wind         = current.getDouble("wind_speed_10m") + " km/h";
        data.humidity     = current.getInt("relative_humidity_2m") + " %";
        data.cloud        = current.getInt("cloud_cover") + " %";
        data.pressure     = current.getDouble("pressure_msl") + " hPa";
        data.visibility   = (current.getDouble("visibility")/1000) + " km";
        data.sunrise      = daily.getJSONArray("sunrise").getString(0);
        data.sunset       = daily.getJSONArray("sunset").getString(0);

        int code = current.getInt("weather_code");
        data.weatherStatus = decodeStatus(code);

        return data;
    }

    private static String decodeStatus(int code) {
        return switch (code) {
            case 0 -> "Clear Sky";
            case 1, 2, 3 -> "Partly Cloudy";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Light Drizzle";
            case 61, 63, 65 -> "Rainy";
            case 71, 73, 75 -> "Snowy";
            case 95 -> "Thunderstorm";
            default -> "Unknown";
        };
    }
}
