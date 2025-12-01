package com.quang.weatherclient.client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WeatherClient extends JFrame {

    private JComboBox<String> cityBox;
    private JTextArea resultArea;

    public WeatherClient() {
        setTitle("Weather Client (TCP) - Java Swing");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cityBox = new JComboBox<>(new String[]{"DANANG", "HANOI", "HCM"});
        JButton btnFetch = new JButton("Lấy dữ liệu");

        btnFetch.addActionListener(e -> fetchWeather());

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        resultArea.setEditable(false);

        JPanel top = new JPanel();
        top.add(cityBox);
        top.add(btnFetch);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private void fetchWeather() {
        String city = cityBox.getSelectedItem().toString();

        try (Socket socket = new Socket("localhost", 7777)) {

            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            pw.println(city);

            String json = br.readLine();

            resultArea.setText(
                    "==== RAW JSON từ Server ====\n\n"
                            + json
                            + "\n\n==== FORMAT ====\n\n"
                            + WeatherParser.parse(json)
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối Server: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new WeatherClient().setVisible(true)
        );
    }
}
