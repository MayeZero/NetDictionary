package org.hao.Client.Request;

import org.hao.Client.View.Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RequestHandler implements Runnable {
//    private Socket socket = null;
//    private RequestType requestType = null;
//
//    public RequestHandler(RequestType requestType) {
//        this.requestType = requestType;
//    }
//
//    @Override
//    public void run() {
//        try{
//            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            out.write(requestType.toString() + "\n");
//            out.flush();
//
//            String result = null;
//            switch (requestType) {
//                case query:
//                    query(socket);
//                    output(socket);
//                    break;
//                case add:
//                    add(socket);
//                    break;
//                case remove:
//                    remove(socket);
//                    break;
//                case addmeanings:
//                    addMeanings(socket);
//                    break;
//                case update:
//                    update(socket);
//                    break;
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
    private Socket socket;
    private final RequestType requestType;
    private final String payload; // e.g., the word to search

    public RequestHandler(RequestType requestType, String payload) {
        this.socket = Client.getSocket();
        this.requestType = requestType;
        this.payload = payload;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connecting to socket...");
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Sending request type: " + requestType);
            out.write(requestType.toString());
            out.newLine();
            out.flush();

            // 根据 requestType 发送具体的 payload
            out.write(payload);
            out.newLine();
            out.flush();
            System.out.println("Sent payload: " + payload);  // 打印出发送的 payload

            String response = in.readLine();
            System.out.println("Received from server: " + response);
            Client.setResponse(this, response);

        } catch (IOException e) {
            e.printStackTrace();
            Client.setResponse(this, "Error: " + e.getMessage());
        }
    }



    public static void handleRequest(Socket s1) throws IOException {
        Scanner scanner = new Scanner(System.in);
        RequestType requestType = RequestType.valueOf(scanner.nextLine());

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
        out.write(requestType.toString() + "\n");
        out.flush();

        switch (requestType) {
            case query:
                query(s1);
                output(s1);
                break;
            case add:
                add(s1);
                output(s1);
                break;
            case remove:
                remove(s1);
                output(s1);
                break;
            case addmeanings:
                addMeanings(s1);
                output(s1);
                break;
            case update:
                update(s1);
                output(s1);
                break;
        }
    }

    public static void query(Socket s1) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
//        out.write(wordToSearch);
        out.newLine();
        out.flush();
    }

    public static void add(Socket s1) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String wordToAdd = scanner.nextLine();
        List<String> meanings = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("add")) {
                break;
            } else {
                meanings.add(input);
//                System.out.println("Added: " + input);
            }
        }

        if (meanings.size() == 0) {
            System.out.println("Please enter meanings for the word");
        }

        System.out.println(wordToAdd);
        System.out.println(String.join(",", meanings));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
        out.write(wordToAdd);
        out.newLine();
        out.write(String.join(",", meanings));
        out.newLine();
        out.flush();
    }

    public static void remove(Socket s1) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String wordToRemove = scanner.nextLine();
        scanner.close();

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
        out.write(wordToRemove);
        out.newLine();
        out.flush();
    }

    public static void addMeanings(Socket s1) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String wordToAdd = scanner.nextLine();
        List<String> meanings = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("addMeanings")) {
                break;
            } else {
                meanings.add(input);
                System.out.println("Added: " + input);
            }
        }

        System.out.println(wordToAdd);
        System.out.println(String.join(",", meanings));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
        out.write(wordToAdd);
        out.newLine();
        out.write(String.join(",", meanings));
        out.newLine();
        out.flush();
    }

    public static void update(Socket s1) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String wordToUpdate = scanner.nextLine();
        String existingMeaning = scanner.nextLine();
        String newMeaning = scanner.nextLine();

        scanner.close();

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
        out.write(wordToUpdate);
        out.newLine();
        out.write(existingMeaning);
        out.newLine();
        out.write(newMeaning);
        out.newLine();
        out.flush();
    }


    public static void output(Socket s1) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        String csv = in.readLine();
        System.out.println("Received CSV: " + csv);

        List<String> words = Arrays.asList(csv.split(","));
        System.out.println("Converted to List: " + words);
        in.close();
    }
}
