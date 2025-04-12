package org.hao.Client.Request;

import java.util.List;

/**
 * Message for request from Client to Server
 * @author Ninghao Zhu 1446180
 */
public class Message {
    private RequestType type;
    private String word;
    private List<String> meanings;
    private String existMeaning;
    private String newMeaning;

    public Message() {}

    public Message(RequestType type, String word, List<String> meanings, String existMeaning, String newMeaning) {
        this.type = type;
        this.word = word;
        this.meanings = meanings;
        this.existMeaning = existMeaning;
        this.newMeaning = newMeaning;
    }

    // Getters & Setters
    public RequestType getType() { return type; }
    public void setType(RequestType type) { this.type = type; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public List<String> getMeanings() { return meanings; }
    public void setMeanings(List<String> meanings) { this.meanings = meanings; }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", word='" + word + '\'' +
                ", meanings=" + meanings +
                '}';
    }

    public String getExistMeaning() {
        return existMeaning;
    }

    public void setExistMeaning(String existMeaning) {
        this.existMeaning = existMeaning;
    }

    public String getNewMeaning() {
        return newMeaning;
    }

    public void setNewMeaning(String newMeaning) {
        this.newMeaning = newMeaning;
    }
}
