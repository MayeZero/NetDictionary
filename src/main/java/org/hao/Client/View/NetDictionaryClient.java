package org.hao.Client.View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

/**
 * Launch ClientGUI and
 * @author Ninghao Zhu 1446180
 */
public class NetDictionaryClient extends Application {
    private Socket socket;

    private BufferedReader input;

    private BufferedWriter output;

    public BufferedReader getInput() {
        return input;
    }

    public void setInput(BufferedReader input) {
        this.input = input;
    }

    public BufferedWriter getOutput() {
        return output;
    }

    public void setOutput(BufferedWriter output) {
        this.output = output;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parameters parameters = getParameters();
            List<String> args = parameters.getRaw();

            if(args.size() < 2){
                System.out.println("Usage: java –jar TCPMultiServer.jar <server-address> <server-port>");
                return;
            }else{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController.fxml"));
                Parent root = loader.load();
                ViewController controller = loader.getController();
                NetDictionaryClient client = new NetDictionaryClient();
                try{
                    socket = new Socket("localhost", Integer.parseInt(args.get(1)));
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    client.socket = socket;
                    client.setInput(input);
                    client.setOutput(output);
                    controller.setSocket(client.socket);
                    controller.setClient(client);
                    primaryStage.setTitle("NetDictionary");
                    primaryStage.setScene(new Scene(root, 840, 500));
                    primaryStage.show();

                    primaryStage.setOnCloseRequest(event -> {
                        event.consume(); // 阻止默认关闭行为
                        showCloseConfirmation(primaryStage);
                    });
                }catch (IOException e) {
                    System.err.println("Please check if the server is running and if the address and port are correct.");
                    e.printStackTrace();  // 输出异常堆栈信息
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Close Popup
    private void showCloseConfirmation(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Any unsaved changes may be lost.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.showAndWait().ifPresent(type -> {
            if (type == yesButton) {
                // Cleanup if needed
                Platform.exit();
                System.exit(0);
            }
        });
    }
}
