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

    public WeatherClient() {
        setTitle("Weather Client (Advanced UI)");
        setSize(420, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));

        cityField = new JTextField(15);
        cityField.setFont(new Font("Arial", Font.PLAIN, 16));

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

        add(mainPanelContainer, BorderLayout.CENTER);

        // ===== GRID PANEL =====
        gridPanel = new JPanel(new GridLayout(4, 2, 12, 12));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        gridPanel.setVisible(false);

        add(gridPanel, BorderLayout.SOUTH);
    }

    private void fetchWeather() {
        try {
            Socket socket = new Socket("localhost", 7777);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String cityName = cityField.getText().trim();

            if (cityName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a city name!");
                return;
            }

            pw.println(cityName);

            // ===== ĐỌC ĐẾN KHI SERVER ĐÓNG SOCKET =====
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString().trim();

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

            mainPanelContainer.setVisible(true);
            gridPanel.setVisible(true);

            largeIcon.setIcon(loadAndResizeIcon(selectMainIcon(data.weatherStatus), 160, 160));
            tempLabel.setText(data.temperature);
            statusLabel.setText(data.weatherStatus);

            gridPanel.removeAll();
            addGridItem("Temp", data.temperature, "temp.png");
            addGridItem("RealFeel", data.realFeel, "feelslike.png");
            addGridItem("Wind", data.wind, "wind.png");
            addGridItem("Humidity", data.humidity, "humidity.png");
            addGridItem("Clouds", data.cloud, "cloud.png");
            addGridItem("Pressure", data.pressure, "pressure.png");
            addGridItem("Visibility", data.visibility, "visibility.png");
            addGridItem("Sunset", data.sunset, "sun.png");

            gridPanel.revalidate();
            gridPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


    private void addGridItem(String title, String value, String iconName) {
        JPanel item = new JPanel(new BorderLayout());
        JLabel iconLabel = new JLabel(loadAndResizeIcon(iconName, 40, 40));
        JLabel text = new JLabel("<html>" + title + "<br><b>" + value + "</b></html>", SwingConstants.CENTER);

        item.add(iconLabel, BorderLayout.WEST);
        item.add(text, BorderLayout.CENTER);

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
        if (status.contains("Rain")) return "rain.png";
        if (status.contains("Fog")) return "fog.png";
        if (status.contains("Snow")) return "snow.png";
        return "weather_main.png";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherClient().setVisible(true));
    }
}
