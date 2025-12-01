package com.quang.weatherclient.client;

import org.json.JSONObject;

public class WeatherParser {

    public static String parse(String json) {

        JSONObject obj = new JSONObject(json);
        JSONObject current = obj.getJSONObject("current");
        JSONObject daily = obj.getJSONObject("daily");

        return ""
                + "Nhiệt độ: " + current.getDouble("temperature_2m") + " °C\n"
                + "RealFeel: " + current.getDouble("apparent_temperature") + " °C\n"
                + "Weather Code: " + current.getInt("weather_code") + "\n"
                + "Độ ẩm: " + current.getInt("relative_humidity_2m") + " %\n"
                + "Mây che phủ: " + current.getInt("cloud_cover") + " %\n"
                + "Áp suất: " + current.getDouble("pressure_msl") + " hPa\n"
                + "Tầm nhìn: " + (current.getDouble("visibility") / 1000) + " km\n"
                + "Gió: " + current.getDouble("wind_speed_10m") + " km/h\n"
                + "Mặt trời mọc: " + daily.getJSONArray("sunrise").getString(0) + "\n"
                + "Mặt trời lặn: " + daily.getJSONArray("sunset").getString(0);
    }
}
