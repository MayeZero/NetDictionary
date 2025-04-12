package org.hao.Server.Data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// DictionaryFile for SaveFile and ReadFile
public class DictionaryFile {
    private static String path;
    private static File file;
    private static final String TEMP_PATH = "temp.json";
    private static File tempFile;
    private static DictionaryFile instance;
    private static final Object lock = new Object();

    public static void initialize(String dictionaryPath){
        if (instance == null) {
            synchronized (DictionaryFile.class) {
                if (instance == null) {
                    path = dictionaryPath;
                    file = new File(path);
                    tempFile = new File(TEMP_PATH);
                    instance = new DictionaryFile();
                }
            }
        }
    }

    public static DictionaryFile getInstance(){
        if (instance == null) {
            throw new IllegalStateException("DictionaryFile has not been initialized. Call initialize(path) first.");
        }
        return instance;
    }

    public static void SaveFile(ConcurrentHashMap<String, List<String>> dictionary){
        if(dictionary == null){
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            synchronized (lock) {
                mapper.writeValue(file, dictionary);
                mapper.writeValue(tempFile, dictionary);
            }
        }
        catch (IOException e) {
            System.err.println("Failed to save dictionary to: " + file.getAbsolutePath());
            System.exit(-1);
        }
    }

    public static ConcurrentHashMap<String, List<String>> ReadFile(){
        ObjectMapper mapper = new ObjectMapper();
        ConcurrentHashMap<String, List<String>> dictionary = null;
        try {
            synchronized (lock) {
                System.out.println("Reading dictionary from: " + file.getAbsolutePath());
                dictionary = mapper.readValue(file, new TypeReference<ConcurrentHashMap<String,List<String>>>(){});
            }
        } catch (FileNotFoundException e){
            System.out.println("Local dictionary do not exist!");
        } catch (IOException e) {
            System.out.println("Failed to read from main dictionary file. Attempting temp backup...");
        }

        if(dictionary == null){
            try {
                synchronized (lock) {
                    dictionary = mapper.readValue(tempFile, new TypeReference<ConcurrentHashMap<String,List<String>>>(){});
                }
            } catch (Exception e){
                dictionary = new ConcurrentHashMap<String,List<String>>();
            }
        }
        SaveFile(dictionary);
        return dictionary;
    }
}
