package org.hao.Client.View;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hao.Client.Request.RequestHandler;
import org.hao.Client.Request.RequestType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private Socket socket;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @FXML
    public void initialize() {
        searchButton.setOnAction(event -> handleSearch());
        addwordButton.setOnAction(event -> handleAddWord());
        removeWordButton.setOnAction(event -> handleRemoveWord());
        addMeaningButton.setOnAction(event -> handleAddMeaning());
        updateMeaningButton.setOnAction(event -> handleUpdateMeaning());
    }

    private void handleSearch() {
        BufferedWriter out = null;
        try {
            socket = new Socket("localhost", 1230);
            setSocket(socket);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String query = searchInputField.getText();
        RequestType requestType = RequestType.query;

        try{
            out.write(requestType.name());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedWriter finalOut = out;
        new Thread(() -> {
            try {
                finalOut.write(query);
                System.out.println(query);
                finalOut.newLine();
                finalOut.flush();

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

    private void handleAddWord() {
        BufferedWriter out = null;
        try {
            socket = new Socket("localhost", 1230);
            setSocket(socket);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String word = addWordField.getText();
        RequestType requestType = RequestType.add;

        List<String> meanings = new ArrayList<>();
        String input = addMeaningsField.getText();

        if (input != null && !input.trim().isEmpty()) {
            String[] parts = input.split(","); // 按逗号+可选空格分割
            meanings.addAll(Arrays.asList(parts));
        }

        try{
            out.write(requestType.name());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(word + " " + meanings);
        BufferedWriter finalOut = out;
        new Thread(() -> {
            try {
                finalOut.write(word);
                finalOut.newLine();
                finalOut.flush();

                for (String meaning : meanings) {
                    finalOut.write(meaning);
                    finalOut.newLine();
                    finalOut.flush();
                }

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

    private void handleRemoveWord() {
        BufferedWriter out = null;
        try {
            socket = new Socket("localhost", 1230);
            setSocket(socket);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String wordToRemove = removeWordField.getText();
        RequestType requestType = RequestType.remove;

        try{
            out.write(requestType.name());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedWriter finalOut = out;
        new Thread(() -> {
            try {
                finalOut.write(wordToRemove);
                finalOut.newLine();
                finalOut.flush();

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

    private void handleAddMeaning() {
        BufferedWriter out = null;
        try {
            socket = new Socket("localhost", 1230);
            setSocket(socket);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String word = existingWordField.getText();
        String newMeaning = AdditionalMeaningField.getText();
        List<String> meanings = new ArrayList<>();
        RequestType requestType = RequestType.addmeanings;
        if (newMeaning != null && !newMeaning.trim().isEmpty()) {
            String[] parts = newMeaning.split(","); // 按逗号+可选空格分割
            meanings.addAll(Arrays.asList(parts));
        }

        try{
            out.write(requestType.name());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedWriter finalOut = out;
        new Thread(() -> {
            try {
                finalOut.write(word);
                finalOut.newLine();
                finalOut.flush();

                for (String meaning : meanings) {
                    finalOut.write(meaning);
                    finalOut.newLine();
                    finalOut.flush();
                }

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

    private void handleUpdateMeaning() {
        BufferedWriter out = null;
        try {
            socket = new Socket("localhost", 1230);
            setSocket(socket);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String word = updateWordField.getText();
        String oldMeaning = updateExistMeaningField.getText();
        String newMeaning = updateNewMeaningField.getText();
        RequestType requestType = RequestType.update;

        try{
            out.write(requestType.name());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedWriter finalOut = out;
        new Thread(() -> {
            try {
                finalOut.write(word);
                finalOut.newLine();
                finalOut.flush();
                finalOut.write(oldMeaning);
                finalOut.newLine();
                finalOut.flush();
                finalOut.write(newMeaning);
                finalOut.newLine();
                finalOut.flush();

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

    public List<String> getSearchResult() {
        BufferedReader in = null;
        try{
            in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        BufferedReader in = NetDictionaryClient.getInput();
        String csv = null;
        try {
            csv = in.readLine();
            System.out.println("Received CSV: " + csv);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> words = Arrays.asList(csv.split(","));
        System.out.println("Converted to List: " + words);
        return words;
    }

    public Socket getSocket() {
        return socket;
    }
}
