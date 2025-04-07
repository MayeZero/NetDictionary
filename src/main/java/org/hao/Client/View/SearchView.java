package org.hao.Client.View;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.hao.Client.Request.RequestType;

import java.io.*;
import java.net.Socket;

public class SearchView {

    @FXML
    private TextField searchField;

    @FXML
    private TextField resultField;

    @FXML
    private Button searchButton;

    private static final String SERVER_ADDRESS = "localhost"; // 或你的服务器 IP
    private static final int SERVER_PORT = 1230; // 和服务端一致

    @FXML
    private void initialize() {
        resultField.setEditable(false);
        searchButton.setOnAction(e -> handleSearch());
    }

    private void handleSearch() {
        String word = searchField.getText().trim();
        if (word.isEmpty()) {
            resultField.setText("请输入要查询的单词");
            return;
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. 发送请求类型
            out.write(RequestType.query.toString());
            out.newLine();
            out.flush();

            // 2. 发送要查询的单词
            out.write(word);
            out.newLine();
            out.flush();

            // 3. 接收服务器返回结果
            String response = in.readLine();
            if (response != null && !response.isEmpty()) {
                resultField.setText("查询结果: " + response);
            } else {
                resultField.setText("未找到该单词");
            }

        } catch (IOException e) {
            resultField.setText("连接服务器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
