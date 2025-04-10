package org.hao.Server.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {
    private static ConcurrentHashMap<String, List<String>> map = null;
    private static final Dictionary dictionary = new Dictionary();
    private final static Object key = new Object();

    public static void initialize(ConcurrentHashMap<String, List<String>> map) {
        if(Dictionary.map == null) {
            Dictionary.map = map;
        }
    }

    public static Dictionary getDictionary() {
        if(map == null) {
            return null;
        }
        return dictionary;
    }

    public static ConcurrentHashMap<String, List<String>> getMap() {
        return map;
    }

    public List<String> getMeaningsForWord(String word){
        if (map.containsKey(word)) {
            return map.get(word);
        }
        return Collections.singletonList("The Word Was Not Found");
    }

    public void addWordAndMeanings(String word, List<String> meanings) {
        if (map.containsKey(word)) {
            return;
        }else{
            synchronized (key){
                map.put(word, meanings);
            }
        }
    }

    public void removeWordAndMeanings(String word) {
        synchronized (key){
            if (map.containsKey(word)){
                map.remove(word);
            }
        }
        return;
    }

    public void addAdditionalMeanings(String word, List<String> meanings) {
        synchronized (key){
            if (map.containsKey(word)) {
                for (String meaning : meanings) {
                    if(!map.get(word).contains(meaning)){
                        map.get(word).add(meaning);
                    }
                }
            }
        }
    }

    public void updateMeaning(String word, String existMeaning, String newMeaning) {
        synchronized (key){
            if (map.containsKey(word)) {
                if (map.get(word).contains(existMeaning)) {
                    map.get(word).remove(existMeaning);
                    map.get(word).add(newMeaning);
                }else{
                    return;
                }
            }
        }
    }
}

