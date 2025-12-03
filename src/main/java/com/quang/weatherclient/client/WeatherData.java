package com.quang.weatherclient.client;

public class WeatherData {
    public String temperature;
    public String realFeel;
    public String wind;
    public String humidity;
    public String cloud;
    public String pressure;
    public String visibility;
    public String sunrise;
    public String sunset;
    public String weatherStatus;
    public double latitude;
    public double longitude;

    // === Forecast 7 ngày ===
    public String[] forecastDates;       // "12-03", "12-04", ...
    public String[] forecastMinTemp;     // "22.5°C"
    public String[] forecastMaxTemp;     // "23.6°C"
    public int[]    forecastWeatherCode; // 80, 45, 3, ...
}
