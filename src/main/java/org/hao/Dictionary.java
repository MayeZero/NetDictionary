package org.hao;
import java.util.*;

public class Dictionary {
    private Map<String, List<String>> dictionaryTable = new HashMap<>();

    public Dictionary() {
        dictionaryTable.put("word1", new ArrayList<>());
        dictionaryTable.get("word1").add("meaning11");
        dictionaryTable.get("word1").add("meaning12");

        dictionaryTable.put("word2", new ArrayList<>());
        dictionaryTable.get("word2").add("meaning21");
        dictionaryTable.get("word2").add("meaning22");
        dictionaryTable.get("word2").add("meaning23");
    }

    public List<String> getMeaningsForWord(String word){
        if (dictionaryTable.containsKey(word)) {
            return dictionaryTable.get(word);
        }
        return Collections.singletonList("The Word Was Not Found");
    }

    public void addWordAndMeanings(String word, List<String> meanings) {
        if (dictionaryTable.containsKey(word)) {
            return;
        }else{
            dictionaryTable.put(word, meanings);
        }
    }

    public void removeWordAndMeanings(String word) {
        if (dictionaryTable.containsKey(word)){
            dictionaryTable.remove(word);
        }else{
            return;
        }
    }

    public void addAdditionalMeanings(String word, List<String> meanings) {
        if (dictionaryTable.containsKey(word)) {
            for (String meaning : meanings) {
                if(!dictionaryTable.get(word).contains(meaning)){
                    dictionaryTable.get(word).add(meaning);
                }
            }
        }else{
            return;
        }
    }

    public void updateMeaning(String word, String existMeaning, String newMeaning) {
        if (dictionaryTable.containsKey(word)) {
            if (dictionaryTable.get(word).contains(existMeaning)) {
                dictionaryTable.get(word).remove(existMeaning);
                dictionaryTable.get(word).add(newMeaning);
            }else{
                return;
            }
        }else{
            return;
        }
    }

    public Map<String, List<String>> getDictionaryTable() {
        return dictionaryTable;
    }
}

