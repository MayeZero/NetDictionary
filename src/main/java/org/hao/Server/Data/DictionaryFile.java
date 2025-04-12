package org.hao.Server.Data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryFile {
    private static String path = null;
    private static File file = null;
    private static DictionaryFile localSave = null;
    private final static Object key = new Object();
    private final static String tempPath = "temp.json";
    private static File temp = null;

    public static void initialize(String path){
        if(DictionaryFile.path == null){
            DictionaryFile.path = path;
            DictionaryFile.file = new File(DictionaryFile.path);
            DictionaryFile.temp = new File(DictionaryFile.tempPath);
            localSave = new DictionaryFile();
        }
    }

    public static DictionaryFile getInstance(){
        return DictionaryFile.localSave;
    }

    public static void SaveFile(ConcurrentHashMap<String, List<String>> dictionary){
        if(dictionary == null){
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            synchronized (key) {
                mapper.writeValue(file, dictionary);
                mapper.writeValue(temp, dictionary);
            }
        }
        catch (IOException e) {
            System.out.println("Fail to save to path: " + path);
            System.out.println("Please enter valid path!");
            System.exit(-1);
        }
    }

    public static ConcurrentHashMap<String, List<String>> ReadFile(){
        ObjectMapper mapper = new ObjectMapper();
        ConcurrentHashMap<String, List<String>> dictionary = null;
        try {
            synchronized (key) {
                System.out.println("Reading from file: " + file.getAbsolutePath());
                dictionary = mapper.readValue(file, new TypeReference<ConcurrentHashMap<String,List<String>>>(){});
            }
        }
        catch (FileNotFoundException e){
            System.out.println("Local save do not exist!");
        }
        catch (IOException e) {
            System.out.println("Fail to read from file! Will start with an empty dictionary");
        }

        if(dictionary == null){
            try {
                synchronized (key) {
                    dictionary = mapper.readValue(temp, new TypeReference<ConcurrentHashMap<String,List<String>>>(){});
                }
            } catch (Exception e){
                dictionary = new ConcurrentHashMap<String,List<String>>();
            }
        }
        SaveFile(dictionary);
        return dictionary;
    }
}
