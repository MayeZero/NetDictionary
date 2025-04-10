package org.hao.Client.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.*;
import java.net.Socket;

public class NetDictionaryClient extends Application {
    public static Client client = null;
    private static Socket socket;

    private static BufferedReader input;

    private static BufferedWriter output;

    @Override
    public void start(Stage primaryStage) throws Exception {
//        NetDictionaryClient.client = new Client();
//        NetDictionaryClient.client.start();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController.fxml"));
            Parent root = loader.load();

            ViewController controller = loader.getController();

            socket = new Socket("localhost", 1230);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            controller.setSocket(socket);
            primaryStage.setTitle("NetDictionary");
            primaryStage.setScene(new Scene(root, 1280, 720));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static BufferedReader getInput() {
        return input;
    }

    public static BufferedWriter getOutput() {
        return output;
    }

    public static Socket getSocket() {
        return socket;
    }
}
