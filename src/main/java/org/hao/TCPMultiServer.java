package org.hao;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.*;
import java.util.*;
import java.io.*;

public class TCPMultiServer {
    public static void main(String[] args) throws IOException{
        // TODO Auto-generated method stub
        ServerSocket s = new ServerSocket(1230);
        while(true) {
            Socket s1 = s.accept();
            Thread T2 = new Thread(()->ServeClient(s1)) ;
            T2.start();
        }
    }

    private static void ServeClient(Socket s1) {
        try {
//            query(s1);

//            add(s1);

//            remove(s1);

            addmeanings(s1);

//              update(s1);
        } catch (SocketException e) {
            System.out.println("SocketException occurred: " + e.getMessage());
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void SaveFile(Dictionary dictionary){
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("dictionary.json"), dictionary);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static Dictionary ReadFile(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File("dictionary.json"), Dictionary.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void query(Socket s1) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        String query = input.readLine();
        System.out.println(query);

        Dictionary dictionary = ReadFile();

        List<String> meaningForWord = dictionary.getMeaningsForWord(query);
        String csv = String.join(",", meaningForWord);

        PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
        out.println(csv);
        out.flush();
        System.out.println("CSV sent to client: " + csv);

        out.close();
        input.close();
        s1.close();
    }

    public static void add(Socket s1) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        Dictionary dictionary = ReadFile();

        String word = input.readLine();
        List<String> meanings = new ArrayList<>();
        while(input.ready()) {
            String line = input.readLine();
            meanings.add(line);
        }

        dictionary.addWordAndMeanings(word, meanings);
        String csv1;
        if(!meanings.isEmpty()){
            csv1 = String.join(",", word, String.join(",", meanings));
            SaveFile(dictionary);
        }else{
            csv1 = "Please enter meanings";
        }

        PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
        out.println(csv1);
        out.flush();
        System.out.println("CSV1 sent to client: " + csv1);

        out.close();
        input.close();
        s1.close();
    }

    public static void remove(Socket s1) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        String removeword = input.readLine();
        System.out.println(removeword);

        Dictionary dictionary = ReadFile();

        dictionary.removeWordAndMeanings(removeword);
        SaveFile(dictionary);
        String csv = removeword;

        PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
        out.println(csv);
        out.flush();
        System.out.println("CSV2 sent to client: " + csv);

        out.close();
        input.close();
        s1.close();
    }

    public static void addmeanings(Socket s1) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        Dictionary dictionary = ReadFile();

        String word = input.readLine();
        List<String> meanings = new ArrayList<>();
        while(input.ready()) {
            String line = input.readLine();
            meanings.add(line);
        }

        dictionary.addAdditionalMeanings(word, meanings);
        String csv1;
        if(!meanings.isEmpty()){
            csv1 = String.join(",", word, String.join(",", meanings));
            SaveFile(dictionary);
        }else{
            csv1 = "Please enter meanings";
        }

        PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
        out.println(csv1);
        out.flush();
        System.out.println("CSV3 sent to client: " + csv1);

        out.close();
        input.close();
        s1.close();
    }

    public static void update(Socket s1) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        Dictionary dictionary = ReadFile();

        String word = input.readLine();
        String existMeaning = input.readLine();
        String newMeaning = input.readLine();

        dictionary.updateMeaning(word, existMeaning, newMeaning);
        SaveFile(dictionary);
        String csv1 = String.join(",", word, existMeaning, newMeaning);
        PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
        out.println(csv1);
        out.flush();
        System.out.println("CSV4 sent to client: " + csv1);

        out.close();
        input.close();
        s1.close();
    }
}

