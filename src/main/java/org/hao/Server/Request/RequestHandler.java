package org.hao.Server.Request;

import org.hao.Server.Common.ThreadsWorkerPool;
import org.hao.Server.Data.Dictionary;
import org.hao.Server.Data.DictionaryFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler implements Runnable {
    private Socket socket = null;

    public RequestHandler(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            RequestType requestType = RequestType.valueOf(br.readLine());
            System.out.println("Received Request: " + requestType);
            switch (requestType){
                case query:
                    query(socket);
                    break;
                case add:
                    add(socket);
                    break;
                case remove:
                    remove(socket);
                    break;
                case addmeanings:
                    addmeanings(socket);
                    break;
                case update:
                    update(socket);
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void query(Socket s1) throws IOException {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            String query = input.readLine();
            System.out.println("Received query: " + query);

            Dictionary dictionary = Dictionary.getDictionary();
            List<String> meaningForWord = dictionary.getMeaningsForWord(query);
            String csv = String.join(",", meaningForWord);

            PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
            out.println(csv);
            out.flush();
            System.out.println("CSV sent to client: " + csv);

            out.close();
            input.close();
            s1.close();
        } catch (IOException e) {
            System.err.println("Error in query task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void add(Socket s1) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            Dictionary dictionary = Dictionary.getDictionary();
            DictionaryFile df = DictionaryFile.getInstance();
            String word = input.readLine();
            System.out.println("Received add: " + word);
            List<String> meanings = new ArrayList<>();
            while(input.ready()) {
                String line = input.readLine();
                meanings.add(line);
            }

            System.out.println("Received add: " + word + " " + meanings);

            dictionary.addWordAndMeanings(word, meanings);
            String csv1;
            if(!meanings.isEmpty()){
                csv1 = String.join(",", word, String.join(",", meanings));
                df.SaveFile(dictionary.getMap());
            } else {
                csv1 = "Please enter meanings";
            }

            PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
            out.println(csv1);
            out.flush();
            System.out.println("CSV1 sent to client: " + csv1);

            out.close();
            input.close();
            s1.close();
        } catch (IOException e) {
            System.err.println("Error in add task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void remove(Socket s1) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            String removeword = input.readLine();
            System.out.println(removeword);

            Dictionary dictionary = Dictionary.getDictionary();
            DictionaryFile df = DictionaryFile.getInstance();
            dictionary.removeWordAndMeanings(removeword);
            df.SaveFile(dictionary.getMap());
            String csv = removeword;

            PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
            out.println(csv);
            out.flush();
            System.out.println("CSV2 sent to client: " + csv);

            out.close();
            input.close();
            s1.close();
        } catch (IOException e) {
            System.err.println("Error in remove task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addmeanings(Socket s1) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            Dictionary dictionary = Dictionary.getDictionary();
            DictionaryFile df = DictionaryFile.getInstance();
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
                df.SaveFile(dictionary.getMap());
            } else {
                csv1 = "Please enter meanings";
            }

            PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
            out.println(csv1);
            out.flush();
            System.out.println("CSV3 sent to client: " + csv1);

            out.close();
            input.close();
            s1.close();
        } catch (IOException e) {
            System.err.println("Error in addmeanings task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void update(Socket s1) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            Dictionary dictionary = Dictionary.getDictionary();
            DictionaryFile df = DictionaryFile.getInstance();

            String word = input.readLine();
            String existMeaning = input.readLine();
            String newMeaning = input.readLine();

            dictionary.updateMeaning(word, existMeaning, newMeaning);
            df.SaveFile(dictionary.getMap());
            String csv1 = String.join(",", word, existMeaning, newMeaning);

            PrintWriter out = new PrintWriter(s1.getOutputStream(), true);
            out.println(csv1);
            out.flush();
            System.out.println("CSV4 sent to client: " + csv1);

            out.close();
            input.close();
            s1.close();
        } catch (IOException e) {
            System.err.println("Error in update task: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
