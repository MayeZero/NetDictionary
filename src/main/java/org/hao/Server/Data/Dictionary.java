package org.hao.Server.Data;

import org.hao.Server.Request.Response;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dictionary for Query, Add, Remove, Add addtional meanings, Update
 * @author Ninghao Zhu 1446180
 */
public class Dictionary {
    private static ConcurrentHashMap<String, List<String>> map = null;
    private static final Dictionary dictionary = new Dictionary();
    private final static Object key = new Object();

    public static void initialize(ConcurrentHashMap<String, List<String>> map) {
        if(Dictionary.map == null) {
            Dictionary.map = map;
        }
    }

    // GetDictionary
    public static Dictionary getDictionary() {
        if(map == null) {
            return null;
        }
        return dictionary;
    }

    // Data Structure to manage Concurrency.
    public static ConcurrentHashMap<String, List<String>> getMap() {
        return map;
    }

    public List<String> getMeaningsForWord(String word){
        if (map.containsKey(word)) {
            return map.get(word);
        }
        return Collections.singletonList("The Word Was Not Found");
    }

    public Response addWordAndMeanings(String word, List<String> meanings) {
        if (map.containsKey(word)) {
            return new Response(false, "Word already exists.");
        } else {
            synchronized (key) {
                map.put(word, meanings);
            }
            return new Response(true, "Word added successfully.");
        }
    }

    public Response removeWordAndMeanings(String word) {
        synchronized (key) {
            if (map.containsKey(word)) {
                map.remove(word);
                return new Response(true, "Removed word: " + word);
            } else {
                return new Response(false, "Word not found: " + word);
            }
        }
    }

    public Response addAdditionalMeanings(String word, List<String> meanings) {
        synchronized (key) {
            if (map.containsKey(word)) {
                List<String> existingMeanings = map.get(word);
                List<String> newlyAddedMeanings = new ArrayList<>();

                for (String meaning : meanings) {
                    if (!existingMeanings.contains(meaning)) {
                        existingMeanings.add(meaning);
                        newlyAddedMeanings.add(meaning);
                    }
                }

                if (!newlyAddedMeanings.isEmpty()) {
                    return new Response(true, String.format("Added meanings to '%s': %s", word, String.join(",", newlyAddedMeanings)));
                } else {
                    return new Response(true, "No new meanings were added for word: " + word + " (all meanings already exist).");
                }
            } else {
                return new Response(false, "Word not found: " + word);
            }
        }
    }

    public Response updateMeaning(String word, String existMeaning, String newMeaning) {
        synchronized (key) {
            if (map.containsKey(word)) {
                if (map.get(word).contains(existMeaning)) {
                    map.get(word).remove(existMeaning);
                    map.get(word).add(newMeaning);
                    return new Response(true, String.format("Updated meaning for '%s': '%s' -> '%s'", word, existMeaning, newMeaning));
                } else {
                    return new Response(false, "Existing meaning not found for word: " + word);
                }
            } else {
                return new Response(false, "Word not found: " + word);
            }
        }
    }

}

