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

public class RequestHandler {
    public static void handleRequest(Socket s1) throws IOException {
        ThreadsWorkerPool threadsWorkerPool = new ThreadsWorkerPool();
        BufferedReader br = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        RequestType requestType = RequestType.valueOf(br.readLine());
        switch (requestType){
            case query:
                threadsWorkerPool.execute(query(s1));
                break;
            case add:
                threadsWorkerPool.execute(add(s1));
                break;
            case remove:
                threadsWorkerPool.execute(remove(s1));
                break;
            case addmeanings:
                threadsWorkerPool.execute(addmeanings(s1));
                break;
            case update:
                threadsWorkerPool.execute(update(s1));
                break;
        }
    }

    public static Runnable query(Socket s1) throws IOException {
        return () -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                String query = input.readLine();
                System.out.println("Received query: " + query);

                Dictionary dictionary = DictionaryFile.ReadFile();
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
        };
    }

    public static Runnable add(Socket s1) {
        return () -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                Dictionary dictionary = DictionaryFile.ReadFile();

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
                    DictionaryFile.SaveFile(dictionary);
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
        };
    }

    public static Runnable remove(Socket s1) {
        return () -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                String removeword = input.readLine();
                System.out.println(removeword);

                org.hao.Server.Data.Dictionary dictionary = DictionaryFile.ReadFile();
                dictionary.removeWordAndMeanings(removeword);
                DictionaryFile.SaveFile(dictionary);
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
        };
    }

    public static Runnable addmeanings(Socket s1) {
        return () -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                org.hao.Server.Data.Dictionary dictionary = DictionaryFile.ReadFile();

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
                    DictionaryFile.SaveFile(dictionary);
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
        };
    }

    public static Runnable update(Socket s1) {
        return () -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                Dictionary dictionary = DictionaryFile.ReadFile();

                String word = input.readLine();
                String existMeaning = input.readLine();
                String newMeaning = input.readLine();

                dictionary.updateMeaning(word, existMeaning, newMeaning);
                DictionaryFile.SaveFile(dictionary);
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
        };
    }
}
