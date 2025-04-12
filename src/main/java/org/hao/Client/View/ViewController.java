package org.hao.Client.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hao.Client.Request.RequestType;
import org.hao.Client.Request.Message;
import org.hao.Client.Request.Response;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Controller for Client GUI.
 * @author Ninghao Zhu 1446180
 */
public class ViewController {

    @FXML
    private TextField searchInputField;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchResultField;

    @FXML
    private TextField addWordField;

    @FXML
    private TextField addMeaningsField;

    @FXML
    private Button addwordButton;

    @FXML
    private Button removeWordButton;

    @FXML
    private TextField existingWordField;

    @FXML
    private TextField AdditionalMeaningField;

    @FXML
    private Button addMeaningButton;

    @FXML
    private TextField updateExistMeaningField;

    @FXML
    private TextField updateWordField;

    @FXML
    private TextField updateNewMeaningField;

    @FXML
    private Button updateMeaningButton;

    @FXML
    private TextField removeWordField;

    @FXML
    private TextField consoleField;

    @FXML
    private Button clearAllButton;

    private Socket socket;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    private NetDictionaryClient client;

    public void setClient(NetDictionaryClient client) {
        this.client = client;
    }

    // Setting events
    @FXML
    public void initialize() {
        searchButton.setOnAction(event -> handleSearch());
        addwordButton.setOnAction(event -> handleAddWord());
        removeWordButton.setOnAction(event -> handleRemoveWord());
        addMeaningButton.setOnAction(event -> handleAddMeaning());
        updateMeaningButton.setOnAction(event -> handleUpdateMeaning());
        clearAllButton.setOnAction(event -> handleClearAll());

    }

    // Query request
    private void handleSearch() {
        String query = searchInputField.getText();
        RequestType requestType = RequestType.query;
        BufferedWriter out = client.getOutput();
        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Message request = new Message(requestType, query, null, null, null);
                String json = mapper.writeValueAsString(request);
                out.write(json);
                out.newLine();
                out.flush();

                Platform.runLater(() -> {
                    searchResultField.setText(getSearchResult().toString());
                    consoleField.setText("Search Word");
                });

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    searchResultField.setText("Error: Unable to retrieve search results.");
                    consoleField.setText("Error: Unable to retrieve search results.");
                });
            }
        }).start();
    }

    // AddWord request
    private void handleAddWord() {
        BufferedWriter out = client.getOutput();
        String word = addWordField.getText();
        RequestType requestType = RequestType.add;

        List<String> meanings = new ArrayList<>();
        String input = addMeaningsField.getText();

        if (input != null && !input.trim().isEmpty()) {
            String[] parts = input.split(","); // 按逗号+可选空格分割
            meanings.addAll(Arrays.asList(parts));
        }

        System.out.println(word + " " + meanings);
        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Message request = new Message(requestType, word, meanings, null, null);
                String json = mapper.writeValueAsString(request);

                out.write(json);
                out.newLine();
                out.flush();

                Platform.runLater(() -> {
                    consoleField.setText(getSearchResult().toString());
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    consoleField.setText("Error: Unable to add word.");
                });
            }
        }).start();
    }

    // RemoveWord request
    private void handleRemoveWord() {
        BufferedWriter out = client.getOutput();
        String wordToRemove = removeWordField.getText();
        RequestType requestType = RequestType.remove;
        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Message request = new Message(requestType, wordToRemove, null, null, null);
                String json = mapper.writeValueAsString(request);

                out.write(json);
                out.newLine();
                out.flush();

                Platform.runLater(() -> {
                    consoleField.setText(getSearchResult().toString());
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    consoleField.setText("Error: Unable to remove word.");
                });
            }
        }).start();
    }

    // AddMeaning request
    private void handleAddMeaning() {
        BufferedWriter out = client.getOutput();
        String word = existingWordField.getText();
        String newMeaning = AdditionalMeaningField.getText();
        List<String> meanings = new ArrayList<>();
        RequestType requestType = RequestType.addmeanings;
        if (newMeaning != null && !newMeaning.trim().isEmpty()) {
            String[] parts = newMeaning.split(","); // 按逗号+可选空格分割
            meanings.addAll(Arrays.asList(parts));
        }

        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Message request = new Message(requestType, word, meanings, null, null);
                String json = mapper.writeValueAsString(request);

                out.write(json);
                out.newLine();
                out.flush();

                Platform.runLater(() -> {
                    consoleField.setText(getSearchResult().toString());
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    consoleField.setText("Error: Unable to add meaning.");
                });
            }
        }).start();
    }

    // UpdateMeaning request
    private void handleUpdateMeaning() {
        BufferedWriter out = client.getOutput();
        String word = updateWordField.getText();
        String existMeaning = updateExistMeaningField.getText();
        String newMeaning = updateNewMeaningField.getText();
        RequestType requestType = RequestType.update;

        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Message request = new Message(requestType, word, null, existMeaning, newMeaning);
                String json = mapper.writeValueAsString(request);

                out.write(json);
                out.newLine();
                out.flush();

                Platform.runLater(() -> {
                    consoleField.setText(getSearchResult().toString());
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    consoleField.setText("Error: Unable to update meaning.");
                });
            }
        }).start();
    }

    // Get Result from Server
    public List<String> getSearchResult() {
        BufferedReader in = client.getInput();
        String json;
        try {
            json = in.readLine();
            System.out.println("Received JSON: " + json);
            ObjectMapper mapper = new ObjectMapper();
            Response response = mapper.readValue(json, Response.class);

            if (response.isSuccess()) {
                String message = response.getMessage(); // e.g. "hi,greetings"
                List<String> words = Arrays.asList(message.split(","));
                System.out.println("Converted to List: " + words);
                return words;
            } else {
                System.out.println("Operation failed: " + response.getMessage());
                return Collections.singletonList("Operation failed: " + response.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClearAll() {
        searchInputField.clear();
        searchResultField.clear();
        addWordField.clear();
        addMeaningsField.clear();
        existingWordField.clear();
        AdditionalMeaningField.clear();
        updateExistMeaningField.clear();
        updateWordField.clear();
        updateNewMeaningField.clear();
        removeWordField.clear();
        consoleField.clear();
    }

    public Socket getSocket() {
        return socket;
    }
}
