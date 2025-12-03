package com.quang.weatherclient.client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WeatherClient extends JFrame {

    private JTextField cityField;
    private JLabel largeIcon;
    private JLabel tempLabel;
    private JLabel statusLabel;
    private JPanel gridPanel;
    private JPanel mainPanelContainer;

    // Panel hiển thị 7 ngày
    private JPanel forecastPanel;

    public WeatherClient() {
        setTitle("Weather Client (Advanced UI)");
        setSize(610, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));

        cityField = new JTextField(15);
        cityField.setFont(new Font("Arial", Font.PLAIN, 16));

        // Autocomplete
        CitySuggestion.attach(cityField, this::fetchWeather);

        // Enter
        cityField.addActionListener(e -> fetchWeather());

        JButton btn = new JButton("Get Weather");
        btn.addActionListener(e -> fetchWeather());

        top.add(cityField);
        top.add(btn);
        add(top, BorderLayout.NORTH);

        // ===== MAIN WEATHER PANEL =====
        mainPanelContainer = new JPanel();
        mainPanelContainer.setLayout(new BoxLayout(mainPanelContainer, BoxLayout.Y_AXIS));
        mainPanelContainer.setVisible(false);

        largeIcon = new JLabel();
        largeIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        tempLabel = new JLabel("-- °C");
        tempLabel.setFont(new Font("Arial", Font.BOLD, 38));
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel("Weather Status");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanelContainer.add(Box.createVerticalStrut(20));
        mainPanelContainer.add(largeIcon);
        mainPanelContainer.add(Box.createVerticalStrut(10));
        mainPanelContainer.add(tempLabel);
        mainPanelContainer.add(Box.createVerticalStrut(5));
        mainPanelContainer.add(statusLabel);
        mainPanelContainer.add(Box.createVerticalStrut(10));

        // ===== FORECAST PANEL (7 ngày) =====
        forecastPanel = new JPanel(new GridLayout(1, 7, 8, 8));
        forecastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        forecastPanel.setVisible(false);
        forecastPanel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Căn giữa

        mainPanelContainer.add(forecastPanel);

        mainPanelContainer.add(Box.createVerticalStrut(15));
        mainPanelContainer.add(forecastPanel);
        mainPanelContainer.add(Box.createVerticalStrut(15));

        add(mainPanelContainer, BorderLayout.CENTER);

        // ===== GRID PANEL (chỉ số chi tiết) =====
        gridPanel = new JPanel(new GridLayout(4, 2, 12, 12));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        gridPanel.setVisible(false);

        add(gridPanel, BorderLayout.SOUTH);
    }

    private void fetchWeather() {
        try {
            String cityName = cityField.getText().trim();

            if (cityName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a city name!");
                return;
            }

            Socket socket = new Socket("localhost", 7777);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw.println(cityName);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString().trim();
            System.out.println("SERVER JSON: " + json);

            if (json.equals("CITY_NOT_FOUND")) {
                JOptionPane.showMessageDialog(this,
                        "City not found: " + cityName,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!json.startsWith("{")) {
                JOptionPane.showMessageDialog(this,
                        "City not found or invalid format: " + cityName,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            WeatherData data = WeatherParser.parse(json);

            // Hiện panel chính
            mainPanelContainer.setVisible(true);
            gridPanel.setVisible(true);

            // Icon lớn + nhiệt độ + trạng thái hiện tại
            largeIcon.setIcon(loadAndResizeIcon(selectMainIcon(data.weatherStatus), 160, 160));
            tempLabel.setText(data.temperature);
            statusLabel.setText(data.weatherStatus);

            // ===== GRID 8 THÔNG SỐ =====
            gridPanel.removeAll();
            addGridItem("Temp",      data.temperature, "temp.png");
            addGridItem("RealFeel",  data.realFeel,    "feelslike.png");
            addGridItem("Wind",      data.wind,        "wind.png");
            addGridItem("Humidity",  data.humidity,    "humidity.png");
            addGridItem("Clouds",    data.cloud,       "cloud.png");
            addGridItem("Pressure",  data.pressure,    "pressure.png");
            addGridItem("Visibility",data.visibility,  "visibility.png");
            addGridItem("Sunset",    data.sunset,      "sun.png");

            gridPanel.revalidate();
            gridPanel.repaint();

            // ===== FORECAST 7 NGÀY =====
            renderForecast(data);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void renderForecast(WeatherData data) {
        if (data.forecastDates == null || data.forecastDates.length == 0) {
            forecastPanel.setVisible(false);
            return;
        }

        forecastPanel.removeAll();

        int n = data.forecastDates.length;

        for (int i = 0; i < n; i++) {

            JPanel dayBox = new JPanel();
            dayBox.setLayout(new BoxLayout(dayBox, BoxLayout.Y_AXIS));
            dayBox.setOpaque(false);
            dayBox.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1)); // Border mảnh

            // ===== Weekday (Mon/Tue/…) =====
            String weekday = WeatherParser.getWeekday(data.originalForecastDates[i]);
            JLabel weekdayLabel = new JLabel(weekday, SwingConstants.CENTER);
            weekdayLabel.setFont(new Font("Arial", Font.BOLD, 11));
            weekdayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // ===== Ngày tháng =====
            JLabel dateLabel = new JLabel(data.forecastDates[i], SwingConstants.CENTER);
            dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));

            // ===== Icon nhỏ (32px) =====
            String status = WeatherParser.decodeStatus(data.forecastWeatherCode[i]);
            JLabel iconLabel = new JLabel(loadAndResizeIcon(selectMainIcon(status), 32, 32));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // ===== Temp Min / Max =====
            JLabel tempLabel = new JLabel(
                    data.forecastMinTemp[i] + " / " + data.forecastMaxTemp[i],
                    SwingConstants.CENTER
            );
            tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            tempLabel.setFont(new Font("Arial", Font.PLAIN, 11));

            // ===== Add vào box =====
            dayBox.add(Box.createVerticalStrut(3));
            dayBox.add(weekdayLabel);
            dayBox.add(dateLabel);
            dayBox.add(Box.createVerticalStrut(4));
            dayBox.add(iconLabel);
            dayBox.add(Box.createVerticalStrut(4));
            dayBox.add(tempLabel);

            forecastPanel.add(dayBox);
        }

        forecastPanel.setVisible(true);
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }

    private void addGridItem(String title, String value, String iconName) {

        JPanel item = new JPanel();
        item.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 5)); // icon + text gần nhau
        item.setOpaque(false);

        JLabel iconLabel = new JLabel(loadAndResizeIcon(iconName, 48, 48));

        JLabel textLabel = new JLabel(
                "<html><b>" + title + "</b><br>" + value + "</html>"
        );

        item.add(iconLabel);
        item.add(textLabel);

        gridPanel.add(item);
    }


    private ImageIcon loadAndResizeIcon(String name, int w, int h) {
        var url = getClass().getResource("/icons/" + name);
        if (url == null) return new ImageIcon();

        Image img = new ImageIcon(url).getImage();
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private String selectMainIcon(String status) {
        if (status.contains("Clear")) return "sun.png";
        if (status.contains("Cloud")) return "cloudy.png";
        if (status.contains("Rain Showers")) return "rain.png";
        if (status.contains("Rain")) return "rain.png";
        if (status.contains("Fog")) return "fog.png";
        if (status.contains("Snow")) return "snow.png";
        if (status.contains("Thunderstorm")) return "storm.png";
        return "weather_main.png";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherClient().setVisible(true));
    }
}
