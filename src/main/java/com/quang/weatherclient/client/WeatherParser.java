package com.quang.weatherclient.client;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class WeatherParser {

    public static WeatherData parse(String json) {

        JSONObject obj = new JSONObject(json);
        JSONObject current = obj.getJSONObject("current");
        JSONObject daily = obj.getJSONObject("daily");

        WeatherData data = new WeatherData();

        // ===== CURRENT =====
        data.temperature  = current.getDouble("temperature_2m") + " °C";
        data.realFeel     = current.getDouble("apparent_temperature") + " °C";
        data.wind         = current.getDouble("wind_speed_10m") + " km/h";
        data.humidity     = current.getInt("relative_humidity_2m") + " %";
        data.cloud        = current.getInt("cloud_cover") + " %";
        data.pressure     = current.getDouble("pressure_msl") + " hPa";
        data.visibility   = (current.getDouble("visibility") / 1000) + " km";
        data.sunrise      = daily.getJSONArray("sunrise").getString(0);
        data.sunset       = daily.getJSONArray("sunset").getString(0);

        int code = current.getInt("weather_code");
        data.weatherStatus = decodeStatus(code);

        // ===== LOCATION =====
        if (obj.has("latitude")) {
            data.latitude = obj.getDouble("latitude");
        }
        if (obj.has("longitude")) {
            data.longitude = obj.getDouble("longitude");
        }

        // ===== FORECAST 7 NGÀY =====
        int n = daily.getJSONArray("time").length();

        data.forecastDates       = new String[n];
        data.forecastMinTemp     = new String[n];
        data.forecastMaxTemp     = new String[n];
        data.forecastWeatherCode = new int[n];
        data.originalForecastDates = new String[n];


        for (int i = 0; i < n; i++) {
            String rawDate = daily.getJSONArray("time").getString(i); // "2025-12-03"
            // Format lại thành "12-03" cho gọn
            if (rawDate.length() >= 10) {
                data.forecastDates[i] = rawDate.substring(5); // "12-03"
            } else {
                data.forecastDates[i] = rawDate;
            }

            double tMax = daily.getJSONArray("temperature_2m_max").getDouble(i);
            double tMin = daily.getJSONArray("temperature_2m_min").getDouble(i);

            data.forecastMaxTemp[i] = String.format("%.1f°C", tMax);
            data.forecastMinTemp[i] = String.format("%.1f°C", tMin);

            data.forecastWeatherCode[i] = daily.getJSONArray("weather_code").getInt(i);
            String fullDate = daily.getJSONArray("time").getString(i);

            data.originalForecastDates[i] = fullDate;

// sửa forecastDates thành tháng-ngày
            if (fullDate.length() >= 10)
                data.forecastDates[i] = fullDate.substring(5); // "12-03"
            else
                data.forecastDates[i] = fullDate;

        }

        return data;
    }

    // Để public cho client có thể dùng cho forecast icon
    public static String decodeStatus(int code) {
        return switch (code) {
            case 0 -> "Clear Sky";
            case 1, 2, 3 -> "Partly Cloudy";
            case 45, 48 -> "Foggy";
            case 51, 53, 55, 56, 57 -> "Drizzle";
            case 61, 63, 65, 66, 67 -> "Rain";
            case 71, 73, 75, 77 -> "Snow";
            case 80, 81, 82 -> "Rain Showers";
            case 85, 86 -> "Snow Showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm w/ Hail";
            default -> "Unknown";
        };
    }

    public static String getWeekday(String fullDate) {
        LocalDate d = LocalDate.parse(fullDate);  // yyyy-MM-dd
        return d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // Mon, Tue...
    }
}
