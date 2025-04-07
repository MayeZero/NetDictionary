package org.hao.Client.Request;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RequestHandler {
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
        Scanner scanner = new Scanner(System.in);
        String wordToSearch = scanner.nextLine();
        scanner.close();

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
        out.write(wordToSearch);
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
