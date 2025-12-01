package com.quang.weatherclient.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WeatherServer {

    private static final int PORT = 7777;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Weather Server running on port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket client) {
        try (
                Socket socket = client;
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String cityName = br.readLine();
            System.out.println("Client yêu cầu: " + cityName);

            double[] coords = GeocodingFetcher.searchCity(cityName);

            if (coords == null) {
                // Gửi 1 dòng báo lỗi rồi đóng socket
                pw.println("CITY_NOT_FOUND");
                return; // try-with-resources sẽ tự đóng socket
            }

            String json = WeatherFetcher.getWeatherByLatLon(coords[0], coords[1]);

            // Gửi JSON (nhiều dòng cũng được), xong thì đóng socket
            pw.println(json);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
