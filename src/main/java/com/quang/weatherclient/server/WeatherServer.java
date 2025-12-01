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
            System.out.println("Weather Server đang chạy trên port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client kết nối: " + client.getInetAddress());

                new Thread(() -> handleClient(client)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket client) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
            PrintWriter pw = new PrintWriter(client.getOutputStream(), true);

            String city = br.readLine(); // nhận DANANG / HANOI / HCM

            System.out.println("Client yêu cầu thành phố: " + city);

            String json = WeatherFetcher.getWeather(city);

            pw.println(json); // trả JSON về client

            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
